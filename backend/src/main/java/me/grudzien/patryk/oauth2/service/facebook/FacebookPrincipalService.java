package me.grudzien.patryk.oauth2.service.facebook;

import org.springframework.security.oauth2.core.user.OAuth2User;

import me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator;

public interface FacebookPrincipalService {

	CustomOAuth2OidcPrincipalUser prepareFacebookPrincipal(final OAuth2FlowDelegator.OAuth2Flow oAuth2Flow, final OAuth2User oAuth2User);
}
