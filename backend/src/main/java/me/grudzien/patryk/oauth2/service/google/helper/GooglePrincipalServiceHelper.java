package me.grudzien.patryk.oauth2.service.google.helper;

import lombok.extern.log4j.Log4j2;

import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

import static me.grudzien.patryk.oauth2.utils.OAuth2OidcAttributesExtractor.getOAuth2AttributeValue;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUserFactory;

@Log4j2
@Component
public class GooglePrincipalServiceHelper {

	public JwtAuthenticationRequest prepareLoginPayload(final OAuth2User oAuth2User, final String password) {
		final String email = getAttribute(StandardClaimNames.EMAIL, oAuth2User);
		return new JwtAuthenticationRequest(email, password, "");
	}

	public UserRegistrationDto prepareRegistrationPayload(final OAuth2User oAuth2User, final String password) {
		return UserRegistrationDto.Builder()
		                          .firstName(getAttribute(StandardClaimNames.GIVEN_NAME, oAuth2User))
		                          .lastName(getAttribute(StandardClaimNames.FAMILY_NAME, oAuth2User))
		                          .email(getAttribute(StandardClaimNames.EMAIL, oAuth2User))
		                          .confirmedEmail(getAttribute(StandardClaimNames.EMAIL, oAuth2User))
		                          .password(password)
		                          .confirmedPassword(password)
		                          .build();
	}

	public CustomOAuth2OidcPrincipalUser preparePrincipalWithStatus(final OAuth2User oAuth2User, final CustomOAuth2OidcPrincipalUser.AccountStatus accountStatus,
	                                                                final String password) {
		return CustomOAuth2OidcPrincipalUserFactory.create(JwtUser.Builder()
		                                                          .firstname(getAttribute(StandardClaimNames.GIVEN_NAME, oAuth2User))
		                                                          .lastname(getAttribute(StandardClaimNames.FAMILY_NAME, oAuth2User))
		                                                          .email(getAttribute(StandardClaimNames.EMAIL, oAuth2User))
		                                                          .password(password)
		                                                          .photoUrl(getAttribute(StandardClaimNames.PICTURE, oAuth2User))
		                                                          .build(),
		                                                   accountStatus);
	}

	public String getAttribute(final String standardClaimName, final OAuth2User oAuth2User) {
		final Map<String, Object> attributes = oAuth2User.getAttributes();
		return getOAuth2AttributeValue(attributes, standardClaimName);
	}
}
