package me.grudzien.patryk;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;

public interface Constants {

	CustomApplicationProperties customApplicationProperties = new CustomApplicationProperties();

	interface Endpoints {
		String AUTH = customApplicationProperties.getEndpoints().getAuthentication().getRoot();
		String REGISTRATION = customApplicationProperties.getEndpoints().getRegistration().getRoot();
		String REFRESH_TOKEN = customApplicationProperties.getEndpoints().getAuthentication().getRefreshToken();
	}

	interface OAuth2 {
		String LOGIN_PAGE = "http://localhost:8080/login";
	}
}
