package me.grudzien.patryk.oauth2.service.google.impl;

import lombok.Getter;
import lombok.Setter;

import org.springframework.core.env.Environment;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StringUtils;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;

import me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.service.RsaAware;
import me.grudzien.patryk.oauth2.service.google.GooglePrincipalService;
import me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator.OAuth2Flow;

import static com.google.common.base.Preconditions.checkNotNull;
import static lombok.AccessLevel.PRIVATE;

public final class GooglePrincipalServiceProxy implements GooglePrincipalService, RsaAware {

    private final GooglePrincipalService googlePrincipalService;

    @Getter(PRIVATE)
    @Setter(PRIVATE)
    private String jwkURL;

    private GooglePrincipalServiceProxy() {
        throw new UnsupportedOperationException("Creating an object of this class with default constructor is not allowed!");
    }

    /**
     * @param googlePrincipalService service implementation responsible for exact business logic.
     * @param environment needed for creating {@link ClientRegistration} object of Google Authorization Provider.
     *                    Reference: {@link GooglePrincipalServiceProxy#getClientRegistration(Environment)}.
     */
    public GooglePrincipalServiceProxy(final GooglePrincipalService googlePrincipalService, final Environment environment) {
        checkNotNull(googlePrincipalService, "googlePrincipalService cannot be null!");
        checkNotNull(environment, "environment cannot be null!");

        this.googlePrincipalService = googlePrincipalService;
        if (StringUtils.isEmpty(jwkURL)) {
            setJwkURL(getClientRegistration(environment).getProviderDetails().getJwkSetUri());
        }
    }

    @Override
    public final CustomOAuth2OidcPrincipalUser prepareGooglePrincipal(final OAuth2Flow oAuth2Flow, final OAuth2User oAuth2User) {
        return googlePrincipalService.prepareGooglePrincipal(oAuth2Flow, oAuth2User);
    }

    @Override
    public final RsaVerifier rsaVerifier(final String keyIdentifier) throws MalformedURLException, JwkException {
        final JwkProvider jwkProvider = new UrlJwkProvider(new URL(getJwkURL()));
        final Jwk jwk = jwkProvider.get(keyIdentifier);
        return new RsaVerifier((RSAPublicKey) jwk.getPublicKey());
    }

    private ClientRegistration getClientRegistration(final Environment environment) {
        return CommonOAuth2Provider.GOOGLE.getBuilder("spring.security.oauth2.client.registration")
                                          .clientId(environment.getProperty("spring.security.oauth2.client.registration.google.client-id"))
                                          .clientSecret(environment.getProperty("spring.security.oauth2.client.registration.google.client-secret"))
                                          .build();
    }
}
