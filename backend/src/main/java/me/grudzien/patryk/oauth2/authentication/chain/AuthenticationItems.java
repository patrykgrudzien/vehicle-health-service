package me.grudzien.patryk.oauth2.authentication.chain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.NONE)
public final class AuthenticationItems {

    private String token;
    private String keyIdentifier;
    private RsaVerifier rsaVerifier;
    private Jwt decodedJwt;
    private Map<String, String> jwtTokenAttributes;

    private static class InstanceHolder {
        private static final AuthenticationItems INSTANCE = new AuthenticationItems();
    }

    static AuthenticationItems getInstance() {
        return InstanceHolder.INSTANCE;
    }

    void clearState() {
        setToken(null);
        setKeyIdentifier(null);
        setRsaVerifier(null);
    }
}
