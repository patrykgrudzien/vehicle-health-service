package me.grudzien.patryk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;

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
	public Heroku heroku() {return new Heroku();}
	public CorsOrigins corsOrigins() {return new CorsOrigins();}

	public class Endpoints {
		public String API_CONTEXT_PATH = customApplicationProperties.getEndpoints().getApiContextPath();
		public String AUTH = customApplicationProperties.getEndpoints().getAuthentication().getRoot();
		public String REGISTRATION = customApplicationProperties.getEndpoints().getRegistration().getRoot();
		public String GENERATE_ACCESS_TOKEN = customApplicationProperties.getEndpoints().getAuthentication().getGenerateAccessToken();
		public String GENERATE_REFRESH_TOKEN = customApplicationProperties.getEndpoints().getAuthentication().getGenerateRefreshToken();
		public String REFRESH_ACCESS_TOKEN = customApplicationProperties.getEndpoints().getAuthentication().getRefreshAccessToken();
		public String REGISTER_USER_ACCOUNT = customApplicationProperties.getEndpoints().getRegistration().getRegisterUserAccount();
		public String REGISTRATION_CONFIRMATION = customApplicationProperties.getEndpoints().getRegistration().getRoot() +
		                                          customApplicationProperties.getEndpoints().getRegistration().getConfirmationUrl();
		public String USER_ALREADY_ENABLED = customApplicationProperties.getEndpoints().getRegistration().getUserAlreadyEnabled();
		public String REGISTRATION_CONFIRMED = customApplicationProperties.getEndpoints().getRegistration().getConfirmed();
		public String CONFIRMATION_TOKEN_NOT_FOUND = customApplicationProperties.getEndpoints().getRegistration().getConfirmedTokenNotFound();
		public String CONFIRMATION_TOKEN_EXPIRED = customApplicationProperties.getEndpoints().getRegistration().getConfirmedTokenExpired();

		public Authentication authentication() {return new Authentication();}

		public class Authentication {
			public String GET_PRINCIPAL_USER = customApplicationProperties.getEndpoints().getAuthentication().getPrincipalUser();
		}
	}

	public class JWT {
		public String TOKEN_SECRET = customApplicationProperties.getJwt().getSecret();
		public String TOKEN_HEADER = customApplicationProperties.getJwt().getHeader();
		public Long ACCESS_TOKEN_EXPIRATION = customApplicationProperties.getJwt().getExpiration();
	}

	public class OAuth2 {
		public String LOGIN_PAGE = customApplicationProperties.getEndpoints().getOAuth2().getLoginPage();
		public Long SHORT_LIVED_TOKEN_EXPIRATION = customApplicationProperties.getEndpoints().getOAuth2().getShortLivedMillis();
		public String USER_LOGGED_IN_USING_GOOGLE = customApplicationProperties.getEndpoints().getOAuth2().getUserLoggedInUsingGoogle();
		public String USER_NOT_FOUND = customApplicationProperties.getEndpoints().getOAuth2().getUserNotFound();
		public String USER_ACCOUNT_IS_LOCKED = customApplicationProperties.getEndpoints().getOAuth2().getUserAccountIsLocked();
		public String USER_IS_DISABLED = customApplicationProperties.getEndpoints().getOAuth2().getUserIsDisabled();
		public String USER_ACCOUNT_IS_EXPIRED = customApplicationProperties.getEndpoints().getOAuth2().getUserAccountIsExpired();
        public String USER_ACCOUNT_ALREADY_EXISTS = customApplicationProperties.getEndpoints().getOAuth2().getUserAccountAlreadyExists();
        public String CREDENTIALS_HAVE_EXPIRED = customApplicationProperties.getEndpoints().getOAuth2().getCredentialsHaveExpired();
        public String JWT_TOKEN_NOT_FOUND = customApplicationProperties.getEndpoints().getOAuth2().getJwtTokenNotFound();
        public String REGISTRATION_PROVIDER_MISMATCH = customApplicationProperties.getEndpoints().getOAuth2().getRegistrationProviderMismatch();
        public String BAD_CREDENTIALS = customApplicationProperties.getEndpoints().getOAuth2().getBadCredentials();
        public String USER_REGISTERED_USING_GOOGLE = customApplicationProperties.getEndpoints().getOAuth2().getUserRegisteredUsingGoogle();
		public String FAILURE_TARGET_URL = customApplicationProperties.getEndpoints().getOAuth2().getFailureTargetUrl();
		public String EXCHANGE_SHORT_LIVED_TOKEN = customApplicationProperties.getEndpoints().getOAuth2().getExchangeShortLivedToken();
	}

	public class Heroku {
		public String HEROKU_BASE_CONTEXT_PATH = customApplicationProperties.getEndpoints().getHeroku().getContextPath();
	}

	public class CorsOrigins {
		public String FRONT_END_MODULE = customApplicationProperties.getCorsOrigins().getFrontEndModule();
		public String BACK_END_MODULE = customApplicationProperties.getCorsOrigins().getBackEndModule();
	}

	public interface FrontendRoutes {
		String UI_CONTEXT_PATH = "/ui";
		String REGISTRATION_FORM = "/registration-form";
		String REGISTRATION_CONFIRMED = "/registration-confirmed";
		String LOGIN = "/login";
		/**
		 * Used for now only in: {@link me.grudzien.patryk.oauth2.util.OAuth2FlowDelegator#determineFlowBasedOnUrl(String)}
		 */
		String LOGOUT = "/logout";
	}

	public interface StaticResources {
		String[] ALL = {"/assets/**", "/index.html", "/favicon.ico", "/static/**", "/static/css/**", "/static/img/**", "/static/js/**", "/public**"};
	}
}
