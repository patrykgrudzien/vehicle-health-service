package me.grudzien.patryk.oauth2.service.google;

import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.auth0.jwk.JwkException;

import java.net.MalformedURLException;

import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator;

public interface GooglePrincipalService {

	CustomOAuth2OidcPrincipalUser finishOAuthFlowAndPreparePrincipal(final OAuth2FlowDelegator.OAuth2Flow oAuth2Flow, final OAuth2User oAuth2User,
	                                                                 final ClientRegistration clientRegistration);

	// TODO: think about exposing this method in the interface
	RsaVerifier rsaVerifier(final String keyIdentifier) throws MalformedURLException, JwkException;
}
