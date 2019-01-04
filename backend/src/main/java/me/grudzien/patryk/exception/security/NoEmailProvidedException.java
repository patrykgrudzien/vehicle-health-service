package me.grudzien.patryk.exception.security;

public class NoEmailProvidedException extends RuntimeException {

    private static final long serialVersionUID = 1355007246913747336L;

    public NoEmailProvidedException(final String message) {
        super(message);
    }
}
