package me.grudzien.patryk.jwt.exception;

public class MissingAuthenticationResultException extends RuntimeException {

    private static final long serialVersionUID = 4206471629405689383L;

    public MissingAuthenticationResultException(final String message) {
        super(message);
    }
}
