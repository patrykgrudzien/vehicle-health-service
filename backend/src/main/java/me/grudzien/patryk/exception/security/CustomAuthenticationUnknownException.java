package me.grudzien.patryk.exception.security;

public class CustomAuthenticationUnknownException extends RuntimeException {

    private static final long serialVersionUID = 7714092561522020288L;

    public CustomAuthenticationUnknownException(final String message) {
        super(message);
    }
}
