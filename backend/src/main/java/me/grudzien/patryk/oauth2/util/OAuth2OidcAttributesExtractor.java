package me.grudzien.patryk.oauth2.util;

import java.util.Map;

public class OAuth2OidcAttributesExtractor {

	public static String getOAuth2AttributeValue(final Map<String, Object> attributes, final String standardClaimName) {
		return (String) attributes.get(standardClaimName);
	}
}
