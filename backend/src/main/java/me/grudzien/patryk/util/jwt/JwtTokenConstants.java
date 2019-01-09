package me.grudzien.patryk.util.jwt;

public final class JwtTokenConstants {

    public static final String AUDIENCE_UNKNOWN = "unknown";
    public static final String AUDIENCE_WEB = "web";
    public static final String AUDIENCE_MOBILE = "mobile";
    public static final String AUDIENCE_TABLET = "tablet";

    public static final String BEARER = "Bearer ";
    public static final int JWT_TOKEN_BEGIN_INDEX = 7;

    private JwtTokenConstants() {
        throw new UnsupportedOperationException("Creating object of this class is not allowed!");
    }

    public enum TokenType {
    	ACCESS_TOKEN, REFRESH_TOKEN
    }
}
