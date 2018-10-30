package me.grudzien.patryk.oauth2.service.google.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.ResponseEntity;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.apache.commons.lang3.StringUtils;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.google.common.base.Preconditions;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.Optional;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.domain.enums.AppFLow;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.exceptions.UnknownOAuth2FlowException;
import me.grudzien.patryk.oauth2.service.google.GooglePrincipalService;
import me.grudzien.patryk.oauth2.service.google.helper.GooglePrincipalServiceHelper;
import me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator;
import me.grudzien.patryk.oauth2.utils.rest.CustomRestTemplateFactory;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.ContextPathsResolver;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

import static me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser.AccountStatus;
import static me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator.OAuth2Flow.LOGIN;
import static me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator.OAuth2Flow.REGISTRATION;
import static me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator.OAuth2Flow.UNKNOWN;
import static me.grudzien.patryk.utils.log.LogMarkers.OAUTH2_MARKER;

@Log4j2
@Service
public class GooglePrincipalServiceImpl implements GooglePrincipalService {

	private final LocaleMessagesCreator localeMessagesCreator;
	private final PropertiesKeeper propertiesKeeper;
	private final ContextPathsResolver contextPathsResolver;
	private final GooglePrincipalServiceHelper googlePrincipalServiceHelper;

	private final RestTemplate customRestTemplate = CustomRestTemplateFactory.createRestTemplate();

	@Setter
	@Getter
	private String jwkURL;

	public GooglePrincipalServiceImpl(final LocaleMessagesCreator localeMessagesCreator, final PropertiesKeeper propertiesKeeper,
	                                  final ContextPathsResolver contextPathsResolver, final GooglePrincipalServiceHelper googlePrincipalServiceHelper) {

		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
		Preconditions.checkNotNull(contextPathsResolver, "contextPathsResolver cannot be null!");
		Preconditions.checkNotNull(googlePrincipalServiceHelper, "googlePrincipalServiceHelper cannot be null!");

		this.localeMessagesCreator = localeMessagesCreator;
		this.propertiesKeeper = propertiesKeeper;
		this.contextPathsResolver = contextPathsResolver;
		this.googlePrincipalServiceHelper = googlePrincipalServiceHelper;
	}

	@Override
	public CustomOAuth2OidcPrincipalUser finishOAuthFlowAndPreparePrincipal(final OAuth2FlowDelegator.OAuth2Flow oAuth2Flow, final OAuth2User oAuth2User,
	                                                                        final ClientRegistration clientRegistration) {
		setJwkURL(clientRegistration.getProviderDetails().getJwkSetUri());
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

	@Override
	public RsaVerifier rsaVerifier(final String keyIdentifier) throws MalformedURLException, JwkException {
		final JwkProvider jwkProvider = new UrlJwkProvider(new URL(this.getJwkURL()));
		// Represents a JSON Web Key (JWK) used to verify the signature of JWTs
		final Jwk jwk = jwkProvider.get(keyIdentifier);
		return new RsaVerifier((RSAPublicKey) jwk.getPublicKey());
	}

	private CustomOAuth2OidcPrincipalUser loginOAuth2Principal(final OAuth2User oAuth2User, final String password) {
		// 1. Request's URL
		final String authEndpoint = propertiesKeeper.endpoints().AUTH;
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
		final String responseMessage = (String) Optional.ofNullable((Map) responseEntity.getBody()).map(map -> map.get("message")).orElse(StringUtils.EMPTY);
		log.info(OAUTH2_MARKER, "Response from customRestTemplate -> ({}), using POST method on \"{}\" endpoint.", responseMessage, authEndpoint);

		return responseEntity.getStatusCode().is2xxSuccessful() ?
				       googlePrincipalServiceHelper.preparePrincipalWithStatus(oAuth2User, AccountStatus.LOGGED, password) :
					   googlePrincipalServiceHelper.preparePrincipalWithStatus(oAuth2User, AccountStatus.NOT_FOUND, password);
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
