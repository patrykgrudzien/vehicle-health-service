package me.grudzien.patryk.oauth2.utils;

import org.springframework.security.oauth2.core.oidc.StandardClaimNames;

import java.util.Map;

public class OAuth2OidcUtils {

	public static String getOAuth2Email(final String registrationId, final Map<String, Object> attributes) {
		return (String) attributes.get(StandardClaimNames.EMAIL);
	}
}
