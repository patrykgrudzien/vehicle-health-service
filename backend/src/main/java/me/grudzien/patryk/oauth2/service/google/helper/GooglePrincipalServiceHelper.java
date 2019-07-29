package me.grudzien.patryk.oauth2.service.google.helper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtUser;
import me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.model.factory.CustomOAuth2OidcPrincipalUserFactory;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.model.enums.RegistrationProvider;

import static me.grudzien.patryk.oauth2.utils.OAuth2OidcAttributesExtractor.getOAuth2AttributeValue;

@Slf4j
public class GooglePrincipalServiceHelper {

	public JwtAuthenticationRequest prepareLoginPayload(final OAuth2User oAuth2User, final String password) {
		final String email = getAttribute(StandardClaimNames.EMAIL, oAuth2User);
		return JwtAuthenticationRequest.Builder()
		                               .email(email)
		                               .password(password)
		                               .idToken(((DefaultOidcUser) oAuth2User).getIdToken().getTokenValue())
		                               .build();
	}

	public UserRegistrationDto prepareRegistrationPayload(final OAuth2User oAuth2User, final String password) {
		return UserRegistrationDto.Builder()
		                          .firstName(getAttribute(StandardClaimNames.GIVEN_NAME, oAuth2User))
		                          .lastName(getAttribute(StandardClaimNames.FAMILY_NAME, oAuth2User))
		                          .email(getAttribute(StandardClaimNames.EMAIL, oAuth2User))
		                          .confirmedEmail(getAttribute(StandardClaimNames.EMAIL, oAuth2User))
		                          .password(password)
		                          .confirmedPassword(password)
		                          .profilePictureUrl(getAttribute(StandardClaimNames.PICTURE, oAuth2User))
                                  .registrationProvider(RegistrationProvider.GOOGLE)
		                          .build();
	}

	public CustomOAuth2OidcPrincipalUser preparePrincipalWithStatus(final OAuth2User oAuth2User, final CustomOAuth2OidcPrincipalUser.AccountStatus accountStatus,
	                                                                final String password) {
		return CustomOAuth2OidcPrincipalUserFactory.create(JwtUser.Builder()
		                                                          .firstname(getAttribute(StandardClaimNames.GIVEN_NAME, oAuth2User))
		                                                          .lastname(getAttribute(StandardClaimNames.FAMILY_NAME, oAuth2User))
		                                                          .email(getAttribute(StandardClaimNames.EMAIL, oAuth2User))
		                                                          .password(password)
		                                                          .profilePictureUrl(getAttribute(StandardClaimNames.PICTURE, oAuth2User))
		                                                          .build(),
		                                                   accountStatus);
	}

	public String getAttribute(final String standardClaimName, final OAuth2User oAuth2User) {
		final Map<String, Object> attributes = oAuth2User.getAttributes();
		return getOAuth2AttributeValue(attributes, standardClaimName);
	}
}
