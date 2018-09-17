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

	public Endpoints endpoints() {
		return new Endpoints();
	}

	public class Endpoints {
		public String AUTH = customApplicationProperties.getEndpoints().getAuthentication().getRoot();
		public String REGISTRATION = customApplicationProperties.getEndpoints().getRegistration().getRoot();
		public String REFRESH_TOKEN = customApplicationProperties.getEndpoints().getAuthentication().getRefreshToken();
		public String REGISTER_USER_ACCOUNT = customApplicationProperties.getEndpoints().getRegistration().getRegisterUserAccount();
	}

	public JWT jwt() {
		return new JWT();
	}

	public class JWT {
		public String TOKEN_HEADER = customApplicationProperties.getJwt().getHeader();
	}

	public OAuth2 oAuth2() {
		return new OAuth2();
	}

	public class OAuth2 {
		public String LOGIN_PAGE = "http://localhost:8080/login";
		public int SHORT_LIVED_MILLIS = customApplicationProperties.getJwt().getShortLivedMillis();
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
	}

	public interface StaticResources {
		String[] ALL = {"/", "/favicon.ico", "/static/**", "/static/*.html", "/static/*.jpg", "/static/css/**", "/static/js/**"};
	}
}
