package me.grudzien.patryk.oauth2.service.google.impl;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser.AccountStatus;
import static me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator.OAuth2Flow.LOGIN;
import static me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator.OAuth2Flow.REGISTRATION;
import static me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator.OAuth2Flow.UNKNOWN;
import static me.grudzien.patryk.oauth2.utils.OAuth2OidcAttributesExtractor.getOAuth2AttributeValue;
import static me.grudzien.patryk.utils.log.LogMarkers.OAUTH2_MARKER;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.domain.enums.AppFLow;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUserFactory;
import me.grudzien.patryk.oauth2.exceptions.UnknownOAuth2FlowException;
import me.grudzien.patryk.oauth2.service.google.GooglePrincipalService;
import me.grudzien.patryk.oauth2.service.google.helper.GooglePrincipalServiceHelper;
import me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator;
import me.grudzien.patryk.oauth2.utils.rest.CustomRestTemplateFactory;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.ContextPathsResolver;

@Log4j2
@Service
public class GooglePrincipalServiceImpl implements GooglePrincipalService {

	private final UserDetailsService userDetailsService;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final PropertiesKeeper propertiesKeeper;
	private final ContextPathsResolver contextPathsResolver;
	private final GooglePrincipalServiceHelper googlePrincipalServiceHelper;

	private final RestTemplate customRestTemplate = CustomRestTemplateFactory.createRestTemplate();

	public GooglePrincipalServiceImpl(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
	                                  final LocaleMessagesCreator localeMessagesCreator, final PropertiesKeeper propertiesKeeper,
	                                  final ContextPathsResolver contextPathsResolver, final GooglePrincipalServiceHelper googlePrincipalServiceHelper) {

		Preconditions.checkNotNull(userDetailsService, "userDetailsService cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
		Preconditions.checkNotNull(contextPathsResolver, "contextPathsResolver cannot be null!");
		Preconditions.checkNotNull(googlePrincipalServiceHelper, "googlePrincipalServiceHelper cannot be null!");

		this.userDetailsService = userDetailsService;
		this.localeMessagesCreator = localeMessagesCreator;
		this.propertiesKeeper = propertiesKeeper;
		this.contextPathsResolver = contextPathsResolver;
		this.googlePrincipalServiceHelper = googlePrincipalServiceHelper;
	}

	@Override
	public CustomOAuth2OidcPrincipalUser finishOAuthFlowAndPreparePrincipal(final OAuth2FlowDelegator.OAuth2Flow oAuth2Flow, final OAuth2User oAuth2User) {
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

		Map<String, Object> attributes = oAuth2User.getAttributes();
		String email = getOAuth2AttributeValue(attributes, StandardClaimNames.EMAIL);
		String photoUrl = getOAuth2AttributeValue(attributes, StandardClaimNames.PICTURE);

		// FIRST implementation
		final String authEndpoint = propertiesKeeper.endpoints().AUTH;
		final JwtAuthenticationRequest jwtAuthenticationRequest = googlePrincipalServiceHelper.prepareLoginPayload(oAuth2User, password);
		final String endpointAbsolutePath = "http://localhost:8088" + authEndpoint;
		final ResponseEntity<Object> responseEntity = customRestTemplate.postForEntity(URI.create(endpointAbsolutePath), jwtAuthenticationRequest, Object.class);

		// SECOND implementation
		final Optional<JwtUser> jwtUser = Optional.ofNullable((JwtUser) userDetailsService.loadUserByUsername(email));
		return jwtUser.map(user -> {
								user.setPhotoUrl(photoUrl);
								return CustomOAuth2OidcPrincipalUserFactory.create(user, AccountStatus.LOGGED);
						  })
		              .orElseThrow(() -> new UsernameNotFoundException(localeMessagesCreator.buildLocaleMessageWithParam("user-not-found-by-email", email)));
	}

	private CustomOAuth2OidcPrincipalUser registerOAuth2Principal(final OAuth2User oAuth2User, final String password) {

		// 1. Request's URL
		final String registrationEndpoint = propertiesKeeper.endpoints().REGISTRATION + propertiesKeeper.endpoints().REGISTER_USER_ACCOUNT;
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
		final String responseMessage = (String) Optional.ofNullable((Map) responseEntity.getBody()).map(map -> map.get("message")).orElse(StringUtils.EMPTY);
		log.info(OAUTH2_MARKER, "Response from customRestTemplate -> ({}), using POST method on \"{}\" endpoint.", responseMessage, registrationEndpoint);

		// 4. Create principal with appropriate account status which is processed
		return responseEntity.getStatusCode().is2xxSuccessful() ?
				       // success registration
				       googlePrincipalServiceHelper.preparePrincipalWithStatus(oAuth2User, AccountStatus.REGISTERED, password) :
					   // in case of exception occurred during registration process
				       googlePrincipalServiceHelper.preparePrincipalWithStatus(oAuth2User, AccountStatus.ALREADY_EXISTS, password);
	}
}
