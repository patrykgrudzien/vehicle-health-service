package me.grudzien.patryk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.configuration.custom.CustomApplicationProperties;

@Component
public final class PropertiesKeeper {

	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public PropertiesKeeper(final CustomApplicationProperties customApplicationProperties) {
		Preconditions.checkNotNull(customApplicationProperties, "customApplicationProperties cannot be null!");
		this.customApplicationProperties = customApplicationProperties;
	}

	public Endpoints endpoints() {return new Endpoints();}
	public JWT jwt() {return new JWT();}
	public OAuth2 oAuth2() {return new OAuth2();}
	public CorsOrigins corsOrigins() {return new CorsOrigins();}

	public class Endpoints {
		public String REGISTRATION = customApplicationProperties.getEndpoints().getRegistration().getRoot();
		public String REGISTER_USER_ACCOUNT = customApplicationProperties.getEndpoints().getRegistration().getRegisterUserAccount();
		public String REGISTRATION_CONFIRMATION = customApplicationProperties.getEndpoints().getRegistration().getRoot() +
		                                          customApplicationProperties.getEndpoints().getRegistration().getConfirmationUrl();
		public String USER_ALREADY_ENABLED = customApplicationProperties.getEndpoints().getRegistration().getUserAlreadyEnabled();
		public String REGISTRATION_CONFIRMED = customApplicationProperties.getEndpoints().getRegistration().getConfirmed();
		public String CONFIRMATION_TOKEN_NOT_FOUND = customApplicationProperties.getEndpoints().getRegistration().getConfirmedTokenNotFound();
		public String CONFIRMATION_TOKEN_EXPIRED = customApplicationProperties.getEndpoints().getRegistration().getConfirmedTokenExpired();
		public String SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT = customApplicationProperties.getEndpoints().getRegistration().getSystemCouldNotEnableUserAccount();
	}

	public class JWT {
		public String TOKEN_SECRET = customApplicationProperties.getJwt().getSecret();
		public Long ACCESS_TOKEN_EXPIRATION = customApplicationProperties.getJwt().getExpiration();
	}

	public class OAuth2 {
		public Long SHORT_LIVED_TOKEN_EXPIRATION = customApplicationProperties.getEndpoints().getOAuth2().getShortLivedMillis();
		public String USER_LOGGED_IN_USING_GOOGLE = customApplicationProperties.getEndpoints().getOAuth2().getUserLoggedInUsingGoogle();
	}

	public class CorsOrigins {
		public String FRONT_END_MODULE = customApplicationProperties.getCorsOrigins().getFrontEndModule();
		public String BACK_END_MODULE = customApplicationProperties.getCorsOrigins().getBackEndModule();
	}
}
