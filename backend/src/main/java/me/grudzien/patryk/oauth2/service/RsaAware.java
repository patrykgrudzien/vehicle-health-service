package me.grudzien.patryk.oauth2.service;

import org.springframework.security.jwt.crypto.sign.RsaVerifier;

import com.auth0.jwk.JwkException;

import java.net.MalformedURLException;

public interface RsaAware {

    /**
     * Method responsible for retrieving {@link com.auth0.jwk.JwkProvider} that contains {@link com.auth0.jwk.Jwk} object.
     * {@link com.auth0.jwk.Jwk} represents JSON Web Key (JWK) used to verify the signature of JWTs.
     *
     * @param keyIdentifier value of <b>kid</b> found in the JWT.
     * @return {@link RsaVerifier} that verifies signatures using an RSA public key.
     * @throws MalformedURLException can be thrown while creating {@link java.net.URL} object.
     * @throws JwkException thrown while {@link com.auth0.jwk.JwkProvider} cannot get {@link com.auth0.jwk.Jwk}.
     */
    RsaVerifier rsaVerifier(final String keyIdentifier) throws MalformedURLException, JwkException;
}
