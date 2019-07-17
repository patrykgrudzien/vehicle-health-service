package me.grudzien.patryk.oauth2.service.google;

import org.springframework.security.oauth2.core.user.OAuth2User;

import me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator;

public interface GooglePrincipalService {

    /**
     * Method responsible for preparation Google principal either in:
     * {@link me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator.OAuth2Flow#LOGIN} or
     * {@link me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator.OAuth2Flow#REGISTRATION} flow.
     *
     * @param oAuth2Flow flow which defines following business logic: either <b>Login</b> or <b>Registration</b>.
     * @param oAuth2User user provided by Spring OAuth2 implementation.
     * @return custom OAuth2 / OpenID Connect principal user.
     */
	CustomOAuth2OidcPrincipalUser prepareGooglePrincipal(final OAuth2FlowDelegator.OAuth2Flow oAuth2Flow, final OAuth2User oAuth2User);
}
