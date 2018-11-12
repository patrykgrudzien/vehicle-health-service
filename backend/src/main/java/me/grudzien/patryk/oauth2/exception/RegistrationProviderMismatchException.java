package me.grudzien.patryk.oauth2.exception;

public class RegistrationProviderMismatchException extends RuntimeException {

    private static final long serialVersionUID = -8628241403988108682L;

    public RegistrationProviderMismatchException(final String message) {
        super(message);
    }
}
