package me.grudzien.patryk.oauth2.service.google;

import org.springframework.security.oauth2.core.user.OAuth2User;

import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator;

public interface GooglePrincipalService {

	CustomOAuth2OidcPrincipalUser finishOAuthFlowAndPreparePrincipal(final OAuth2FlowDelegator.OAuth2Flow oAuth2Flow, final OAuth2User oAuth2User);
}
