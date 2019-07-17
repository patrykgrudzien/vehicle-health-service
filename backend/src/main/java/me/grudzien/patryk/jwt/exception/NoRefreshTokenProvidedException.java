package me.grudzien.patryk.jwt.exception;

public class NoRefreshTokenProvidedException extends RuntimeException {

    private static final long serialVersionUID = -317472281567151673L;

    public NoRefreshTokenProvidedException(final String message) {
        super(message);
    }
}
