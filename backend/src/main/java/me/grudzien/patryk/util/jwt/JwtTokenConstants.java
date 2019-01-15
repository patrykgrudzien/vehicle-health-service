package me.grudzien.patryk.util.jwt;

import io.jsonwebtoken.SignatureAlgorithm;

public final class JwtTokenConstants {

    static final String AUDIENCE_UNKNOWN = "unknown";
    public static final String AUDIENCE_WEB = "web";
    static final String AUDIENCE_MOBILE = "mobile";
    static final String AUDIENCE_TABLET = "tablet";

    public static final String BEARER = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Authorization";
    public static final int JWT_TOKEN_BEGIN_INDEX = 7;

    public static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    private JwtTokenConstants() {
        throw new UnsupportedOperationException("Creating object of this class is not allowed!");
    }

    public enum TokenType {
    	ACCESS_TOKEN, REFRESH_TOKEN
    }
}
