package me.grudzien.patryk.oauth2.util;

import java.util.Map;

public final class OAuth2OidcAttributesExtractor {

    private OAuth2OidcAttributesExtractor() {
        throw new UnsupportedOperationException("Creating object of this class is not allowed!");
    }

	public static String getOAuth2AttributeValue(final Map<String, Object> attributes, final String standardClaimName) {
		return (String) attributes.get(standardClaimName);
	}
}
