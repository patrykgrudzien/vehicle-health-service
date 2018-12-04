package me.grudzien.patryk.exception.security;

public class CannotRefreshAccessTokenException extends RuntimeException {

    private static final long serialVersionUID = -6665291095663299558L;

    public CannotRefreshAccessTokenException(final String message) {
        super(message);
    }
}
