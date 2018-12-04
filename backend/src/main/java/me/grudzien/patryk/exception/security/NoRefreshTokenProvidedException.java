package me.grudzien.patryk.exception.security;

public class NoRefreshTokenProvidedException extends RuntimeException {

    private static final long serialVersionUID = -317472281567151673L;

    public NoRefreshTokenProvidedException(final String message) {
        super(message);
    }
}
