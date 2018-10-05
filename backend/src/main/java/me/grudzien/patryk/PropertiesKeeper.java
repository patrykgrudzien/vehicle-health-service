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
		public String AUTH = customApplicationProperties.getEndpoints().getAuthentication().getRoot();
		public String REGISTRATION = customApplicationProperties.getEndpoints().getRegistration().getRoot();
		public String REFRESH_TOKEN = customApplicationProperties.getEndpoints().getAuthentication().getRefreshToken();
		public String REGISTER_USER_ACCOUNT = customApplicationProperties.getEndpoints().getRegistration().getRegisterUserAccount();
		public String REGISTRATION_CONFIRMATION = customApplicationProperties.getEndpoints().getRegistration().getRoot() +
		                                          customApplicationProperties.getEndpoints().getRegistration().getConfirmationUrl();
		public String USER_ALREADY_ENABLED = customApplicationProperties.getEndpoints().getRegistration().getUserAlreadyEnabled();
		public String REGISTRATION_CONFIRMED = customApplicationProperties.getEndpoints().getRegistration().getConfirmed();
		public String CONFIRMATION_TOKEN_NOT_FOUND = customApplicationProperties.getEndpoints().getRegistration().getConfirmedTokenNotFound();
		public String CONFIRMATION_TOKEN_EXPIRED = customApplicationProperties.getEndpoints().getRegistration().getConfirmedTokenExpired();
	}

	public class JWT {
		public String TOKEN_HEADER = customApplicationProperties.getJwt().getHeader();
	}

	public class OAuth2 {
		public String LOGIN_PAGE = customApplicationProperties.getEndpoints().getOAuth2().getLoginPage();
		public Long SHORT_LIVED_MILLIS = customApplicationProperties.getEndpoints().getOAuth2().getShortLivedMillis();
		public String USER_LOGGED_IN_USING_GOOGLE = customApplicationProperties.getEndpoints().getOAuth2().getUserLoggedInUsingGoogle();
		public String FAILURE_TARGET_URL = customApplicationProperties.getEndpoints().getOAuth2().getFailureTargetUrl();
	}

	public class Heroku {
		public String HEROKU_BASE_CONTEXT_PATH = customApplicationProperties.getEndpoints().getHeroku().getContextPath();
	}

	public class CorsOrigins {
		public String FRONT_END_MODULE = customApplicationProperties.getCorsOrigins().getFrontEndModule();
		public String BACK_END_MODULE = customApplicationProperties.getCorsOrigins().getBackEndModule();
	}

	public interface FrontendRoutes {
		String ABOUT_ME = "/about-me";
		String REGISTRATION_FORM = "/registration-form";
		String REGISTRATION_CONFIRMED = "/registration-confirmed";
		String REGISTRATION_CONFIRMED_WILDCARD = "/registration-confirmed/**";
		String LOGIN = "/login";
		String MAIN_BOARD = "/main-board";
		String MAIN_BOARD_WILDCARD = "/main-board/**";
		String AUTHENTICATION_REQUIRED = "/authentication-required";
		/**
		 * Used for now only in: {@link me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator#determineFlowBasedOnUrl(String)}
		 */
		String LOGOUT = "/logout";
	}

	public interface StaticResources {
		String[] ALL = {"/", "/favicon.ico", "/static/**", "/static/*.html", "/static/*.jpg", "/static/css/**", "/static/js/**"};
	}
}
