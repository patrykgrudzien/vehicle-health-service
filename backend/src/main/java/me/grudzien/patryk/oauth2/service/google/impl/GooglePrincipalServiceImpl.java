package me.grudzien.patryk.oauth2.service.google.impl;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Preconditions;

import java.util.Map;
import java.util.Optional;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUserFactory;
import me.grudzien.patryk.oauth2.exceptions.UnknownDelegateException;
import me.grudzien.patryk.oauth2.service.google.GooglePrincipalService;
import me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator;
import me.grudzien.patryk.oauth2.utils.rest.CustomRestTemplateFactory;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

import static me.grudzien.patryk.oauth2.utils.OAuth2OidcAttributesExtractor.getOAuth2AttributeValue;

@Log4j2
@Service
public class GooglePrincipalServiceImpl implements GooglePrincipalService {

	private final UserDetailsService userDetailsService;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final PropertiesKeeper propertiesKeeper;
	private final PasswordEncoder passwordEncoder;

	private final RestTemplate restTemplate = CustomRestTemplateFactory.createRestTemplate();

	public GooglePrincipalServiceImpl(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
	                                  final LocaleMessagesCreator localeMessagesCreator, final PropertiesKeeper propertiesKeeper,
	                                  @Lazy final PasswordEncoder passwordEncoder) {
		Preconditions.checkNotNull(userDetailsService, "userDetailsService cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
		Preconditions.checkNotNull(passwordEncoder, "passwordEncoder cannot be null!");

		this.userDetailsService = userDetailsService;
		this.localeMessagesCreator = localeMessagesCreator;
		this.propertiesKeeper = propertiesKeeper;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public CustomOAuth2OidcPrincipalUser finishOAuthFlowAndPreparePrincipal(final OAuth2FlowDelegator.OAuth2Flow oAuth2Flow, final OAuth2User oAuth2User) {

		final Map<String, Object> attributes = oAuth2User.getAttributes();
		final String email = getOAuth2AttributeValue(attributes, StandardClaimNames.EMAIL);

		switch (oAuth2Flow) {
			case LOGIN:
				final String photoUrl = getOAuth2AttributeValue(attributes, StandardClaimNames.PICTURE);
				final Optional<JwtUser> jwtUser = Optional.ofNullable((JwtUser) userDetailsService.loadUserByUsername(email));
				return jwtUser.map(user -> {
									  user.setPhotoUrl(photoUrl);
									  return CustomOAuth2OidcPrincipalUserFactory.create(user, CustomOAuth2OidcPrincipalUser.AccountStatus.LOGGED);
								  })
				              .orElseThrow(() -> new UsernameNotFoundException(
						              localeMessagesCreator.buildLocaleMessageWithParam("user-not-found-by-email", email)));
			case REGISTRATION:
				final String firstName = getOAuth2AttributeValue(attributes, StandardClaimNames.GIVEN_NAME);
				final String lastName = getOAuth2AttributeValue(attributes, StandardClaimNames.FAMILY_NAME);
				final String password = getOAuth2AttributeValue(attributes, StandardClaimNames.SUB);
				final String encodedPassword = passwordEncoder.encode(password);
				final UserRegistrationDto userRegistrationDto = UserRegistrationDto.Builder()
				                                                                   .firstName(firstName)
				                                                                   .lastName(lastName)
				                                                                   .email(email)
				                                                                   .confirmedEmail(email)
				                                                                   .password(encodedPassword)
				                                                                   .confirmedPassword(encodedPassword)
				                                                                   .build();

				final String registrationEndpoint = propertiesKeeper.endpoints().REGISTRATION + propertiesKeeper.endpoints().REGISTER_USER_ACCOUNT;

				// TODO: consider to return maybe ResponseEntity<CustomResponse> from controller
				restTemplate.postForLocation("http://localhost:8088" + registrationEndpoint, userRegistrationDto);
				return CustomOAuth2OidcPrincipalUser.Builder(JwtUser.Builder()
				                                                    .firstname(firstName)
				                                                    .lastname(lastName)
				                                                    .email(email)
				                                                    .password(encodedPassword)
				                                                    .build())
				                                    .accountStatus(CustomOAuth2OidcPrincipalUser.AccountStatus.REGISTERED)
				                                    .build();
			case UNKNOWN:
				throw new UnknownDelegateException(localeMessagesCreator.buildLocaleMessage("unknown-delegate-exception"));
		}
		return null;
		// TODO: check cache at the end (after principal user is returned)
	}
}
