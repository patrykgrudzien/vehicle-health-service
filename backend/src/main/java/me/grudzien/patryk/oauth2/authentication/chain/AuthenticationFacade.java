package me.grudzien.patryk.oauth2.authentication.chain;

import me.grudzien.patryk.oauth2.service.google.impl.GooglePrincipalServiceProxy;

public final class AuthenticationFacade {

    private AuthenticationFacade() {
        throw new UnsupportedOperationException("Creating object of this class is not allowed!");
    }

    public static AbstractAuthenticationStepBuilder buildAuthenticationFlow(final GooglePrincipalServiceProxy googlePrincipalServiceProxy) {
        return new FirstStep(
                new SecondStep(
                        new Third(null, googlePrincipalServiceProxy)));
    }
}
