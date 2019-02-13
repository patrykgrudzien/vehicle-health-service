package me.grudzien.patryk.oauth2.authentication.chain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

import java.util.Map;

import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.oauth2.authentication.CustomAuthenticationToken;

@Log4j2
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.NONE)
public final class AuthenticationItems {

    private String token;
    private String keyIdentifier;
    private RsaVerifier rsaVerifier;
    private Jwt decodedJwt;
    private Map<Object, Object> jwtTokenAttributes;
    private String email;
    private String subjectIdentifier;
    private JwtUser jwtUser;
    private CustomAuthenticationToken customAuthenticationToken;

    private static class InstanceHolder {
        private static final AuthenticationItems INSTANCE = new AuthenticationItems();
    }

    static AuthenticationItems getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Don't clear {@link CustomAuthenticationToken} as it should be returned in:
     * {@link me.grudzien.patryk.oauth2.authentication.CustomAuthenticationProvider#authenticate(Authentication)} once,
     * {@link AuthenticationResult.Status} is {@link AuthenticationResult.Status#OK}.
     */
    void clearState() {
        setToken(null);
        setKeyIdentifier(null);
        setRsaVerifier(null);
        setDecodedJwt(null);
        setJwtTokenAttributes(null);
        setEmail(null);
        setSubjectIdentifier(null);
        setJwtUser(null);
        log.info("AuthenticationItems' state was successfully cleared out.");
    }
}
