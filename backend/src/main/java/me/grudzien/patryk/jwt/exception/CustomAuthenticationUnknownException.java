package me.grudzien.patryk.jwt.exception;

public class CustomAuthenticationUnknownException extends RuntimeException {

    private static final long serialVersionUID = 7714092561522020288L;

    public CustomAuthenticationUnknownException(final String message) {
        super(message);
    }
}
