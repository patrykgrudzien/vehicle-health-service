package me.grudzien.patryk;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;

public interface Constants {

	CustomApplicationProperties customProps = new CustomApplicationProperties();

	interface Endpoints {
		String AUTH = customProps.getEndpoints().getAuthentication().getRoot();
		String REGISTRATION = customProps.getEndpoints().getRegistration().getRoot();
		String REFRESH_TOKEN = customProps.getEndpoints().getAuthentication().getRefreshToken();
	}

	interface OAuth2 {
		String LOGIN_PAGE = "http://localhost:8080/login";
		int SHORT_LIVED_MILLIS = customProps.getJwt().getShortLivedMillis();
	}

	interface FrontendRoutes {
		String ABOUT_ME = "/about-me";
		String REGISTRATION_FORM = "/registration-form";
		String REGISTRATION_CONFIRMED = "/registration-confirmed";
		String REGISTRATION_CONFIRMED_WILDCARD = "/registration-confirmed/**";
		String LOGIN = "/login";
		String MAIN_BOARD = "/main-board";
		String MAIN_BOARD_WILDCARD = "/main-board/**";
		String AUTHENTICATION_REQUIRED = "/authentication-required";
	}
}
