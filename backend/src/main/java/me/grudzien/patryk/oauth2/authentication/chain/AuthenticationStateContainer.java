package me.grudzien.patryk.oauth2.authentication.chain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.security.jwt.crypto.sign.RsaVerifier;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.NONE)
final class AuthenticationStateContainer {

    private String token;
    private String keyIdentifier;
    private RsaVerifier rsaVerifier;
    private Throwable throwable;

    private static class InstanceHolder {
        private static final AuthenticationStateContainer INSTANCE = new AuthenticationStateContainer();
    }

    static AuthenticationStateContainer getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
