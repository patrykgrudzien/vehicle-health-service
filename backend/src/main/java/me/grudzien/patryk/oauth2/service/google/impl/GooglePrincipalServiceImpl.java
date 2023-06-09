package me.grudzien.patryk.oauth2.service.google.impl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Maps;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions;
import me.grudzien.patryk.oauth2.exception.UnknownOAuth2FlowException;
import me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.service.google.GooglePrincipalService;
import me.grudzien.patryk.oauth2.service.google.helper.GooglePrincipalServiceHelper;
import me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator;
import me.grudzien.patryk.oauth2.utils.rest.CustomRestTemplateFactory;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.utils.appplication.AppFLow;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.ContextPathsResolver;
import me.grudzien.patryk.utils.web.MvcPattern;
import me.grudzien.patryk.utils.web.model.CustomResponse.ResponseProperties;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.AUTHENTICATION_RESOURCE_ROOT;
import static me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser.AccountStatus;
import static me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator.OAuth2Flow.LOGIN;
import static me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator.OAuth2Flow.REGISTRATION;
import static me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator.OAuth2Flow.UNKNOWN;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.CREATE_USER_ACCOUNT;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.REGISTRATION_RESOURCE_ROOT;

@Slf4j
@Service
public class GooglePrincipalServiceImpl implements GooglePrincipalService {

	private final LocaleMessagesCreator localeMessagesCreator;
	private final ContextPathsResolver contextPathsResolver;

    private final GooglePrincipalServiceHelper googlePrincipalServiceHelper = new GooglePrincipalServiceHelper();
    private final RestTemplate customRestTemplate = CustomRestTemplateFactory.createRestTemplate();

	public GooglePrincipalServiceImpl(final LocaleMessagesCreator localeMessagesCreator,
                                      final ContextPathsResolver contextPathsResolver) {

		checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		checkNotNull(contextPathsResolver, "contextPathsResolver cannot be null!");

		this.localeMessagesCreator = localeMessagesCreator;
		this.contextPathsResolver = contextPathsResolver;
	}

	@Override
	public CustomOAuth2OidcPrincipalUser prepareGooglePrincipal(final OAuth2FlowDelegator.OAuth2Flow oAuth2Flow, final OAuth2User oAuth2User) {
		/**
		 * Taking {@link org.springframework.security.oauth2.core.oidc.StandardClaimNames.SUB} for password
		 * as google cannot directly return real user password.
		 */
		final String password = googlePrincipalServiceHelper.getAttribute(StandardClaimNames.SUB, oAuth2User);

		return Match(oAuth2Flow).of(
				Case($(is(LOGIN)), () -> this.loginOAuth2Principal(oAuth2User, password)),
				Case($(is(REGISTRATION)), () -> this.registerOAuth2Principal(oAuth2User, password)),
				Case($(is(UNKNOWN)), flow -> {
					throw new UnknownOAuth2FlowException(localeMessagesCreator.buildLocaleMessage("unknown-oauth2-flow-exception"));
				}));
	}

	private CustomOAuth2OidcPrincipalUser loginOAuth2Principal(final OAuth2User oAuth2User, final String password) {
		// 1. Request's URL
//		final String authEndpoint = propertiesKeeper.endpoints().API_CONTEXT_PATH + propertiesKeeper.endpoints().AUTH;
		final String authEndpoint = MvcPattern.of(AUTHENTICATION_RESOURCE_ROOT, AuthenticationResourceDefinitions.LOGIN);
		// 2. Request's payload
		final JwtAuthenticationRequest jwtAuthenticationRequest = googlePrincipalServiceHelper.prepareLoginPayload(oAuth2User, password);

		// 3. Call endpoint as POST request
		final String endpointAbsolutePath = "http://localhost:8088" + authEndpoint;
		/**
		 * {@code customRestTemplate} is specific to this case!
		 * Look inside:
		 * {@link me.grudzien.patryk.oauth2.utils.rest.CustomRestTemplateFactory#createRestTemplate()}
		 */
		final ResponseEntity<Object> responseEntity = customRestTemplate.postForEntity(URI.create(endpointAbsolutePath), jwtAuthenticationRequest, Object.class);

        return responseEntity.getStatusCode().is2xxSuccessful() ?
                // successful login
                handle2xxSuccessfulResponse(responseEntity, oAuth2User, AccountStatus.LOGGED, password, authEndpoint) :
                // in case of exception occurred during login process
                handle4xxClientError(responseEntity, oAuth2User, password, authEndpoint);
	}

    private CustomOAuth2OidcPrincipalUser registerOAuth2Principal(final OAuth2User oAuth2User, final String password) {
		// 1. Request's URL
		final String registrationEndpoint = MvcPattern.of(REGISTRATION_RESOURCE_ROOT, CREATE_USER_ACCOUNT);
		// 2. Request's payload
		final UserRegistrationDto userRegistrationDto = googlePrincipalServiceHelper.prepareRegistrationPayload(oAuth2User, password);
		// 3. Call endpoint as POST request
		final String endpointAbsolutePath = contextPathsResolver.determineUrlFor(AppFLow.REGISTER_OAUTH2_PRINCIPAL) + registrationEndpoint;
		/**
		 * {@code customRestTemplate} is specific to this case!
		 * Look inside:
		 * {@link me.grudzien.patryk.oauth2.utils.rest.CustomRestTemplateFactory#createRestTemplate()}
		 */
		final ResponseEntity<Object> responseEntity = customRestTemplate.postForEntity(URI.create(endpointAbsolutePath), userRegistrationDto, Object.class);

		// 4. Create principal with appropriate account status which is processed
		return responseEntity.getStatusCode().is2xxSuccessful() ?
                // successful registration
                handle2xxSuccessfulResponse(responseEntity, oAuth2User, AccountStatus.REGISTERED, password, registrationEndpoint) :
                // in case of exception occurred during registration process
                handle4xxClientError(responseEntity, oAuth2User, password, registrationEndpoint);
	}

    /**
     * Method which handles successfully passed OAuth2 flow.
     *
     * @param responseEntity object to get body parameters from
     * @param oAuth2User OAuth2 User
     * @param accountStatus {@link AccountStatus}
     * @param password password taken from Social Provider
     * @param logParams additional logging parameters
     */
    private CustomOAuth2OidcPrincipalUser handle2xxSuccessfulResponse(final ResponseEntity<Object> responseEntity, final OAuth2User oAuth2User,
                                                                      final AccountStatus accountStatus, final String password, final String... logParams) {
	    final String responseMessage = extractPropertyFromResponse(responseEntity, ResponseProperties.MESSAGE.getProperty(),
                                                                   accountStatus == AccountStatus.LOGGED ? "Login passed successfully." : "Registration passed successfully.");
	    log.info("Response from customRestTemplate -> ({}), using POST method on \"{}\" endpoint.", responseMessage, logParams);
        return googlePrincipalServiceHelper.preparePrincipalWithStatus(oAuth2User, accountStatus, password);
    }

    /**
     * Method which handles all errors that occurred during OAuth2 flow.
     *
     * @param responseEntity object to get body parameters from
     * @param oAuth2User OAuth2 User
     * @param password password taken from Social Provider
     * @param logParams additional logging parameters
     */
    private CustomOAuth2OidcPrincipalUser handle4xxClientError(final ResponseEntity<Object> responseEntity, final OAuth2User oAuth2User,
                                                               final String password, final String... logParams) {
        // preparing default value when there is no property in response body
        final LinkedHashMap<String, String> defaultMapOnMissingProperty = Maps.newLinkedHashMap();
        defaultMapOnMissingProperty.put(ResponseProperties.ACCOUNT_STATUS.getProperty(), AccountStatus.NO_STATUS_PROVIDED_IN_EXCEPTION_HANDLER.getAccountStatus());

        final LinkedHashMap<String, String> propertiesMap = extractPropertyFromResponse(responseEntity, ResponseProperties.ACCOUNT_STATUS.getProperty(),
                                                                                        defaultMapOnMissingProperty);

        final AccountStatus ACCOUNT_STATUS = Enum.valueOf(AccountStatus.class, propertiesMap.get(ResponseProperties.ACCOUNT_STATUS.getProperty()));
        log.error("An error occurred -> ({}) from customRestTemplate, using POST method on \"{}\" endpoint.",
                  ACCOUNT_STATUS.getAccountStatus(), logParams);

        return googlePrincipalServiceHelper.preparePrincipalWithStatus(oAuth2User, ACCOUNT_STATUS, password);
    }

    /**
     * Utility method used to extract specified property from response body.
     *
     * @param responseEntity object to get body parameters from
     * @param property name of the property which is used to get value from response body
     * @param defaultValueOnMissingProperty default value when response body doesn't contain provided property
     */
    @SuppressWarnings("unchecked")
    private <T> T extractPropertyFromResponse(final ResponseEntity<Object> responseEntity, final String property, final T defaultValueOnMissingProperty) {
        return (T) Optional.ofNullable((Map) responseEntity.getBody())
                           .map(map -> map.get(property))
                           .orElseGet(() -> {
                               log.warn("No value found in the response entity for provided property -> \"{}\". Returning default value.", property);
                               return defaultValueOnMissingProperty;
                           });
    }
}
