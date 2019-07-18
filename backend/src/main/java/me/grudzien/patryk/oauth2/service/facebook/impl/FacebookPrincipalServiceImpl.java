package me.grudzien.patryk.oauth2.service.facebook.impl;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.service.facebook.FacebookPrincipalService;
import me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator;
import me.grudzien.patryk.authentication.service.impl.MyUserDetailsService;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

@Log4j2
@Service
public class FacebookPrincipalServiceImpl implements FacebookPrincipalService {

	private final UserDetailsService userDetailsService;
	private final LocaleMessagesCreator localeMessagesCreator;

    public FacebookPrincipalServiceImpl(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
                                        final LocaleMessagesCreator localeMessagesCreator) {
		Preconditions.checkNotNull(userDetailsService, "userDetailsService cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		this.userDetailsService = userDetailsService;
		this.localeMessagesCreator = localeMessagesCreator;
	}

	@Override
	public CustomOAuth2OidcPrincipalUser prepareFacebookPrincipal(final OAuth2FlowDelegator.OAuth2Flow oAuth2Flow, final OAuth2User oAuth2User) {
		return null;
	}
}
