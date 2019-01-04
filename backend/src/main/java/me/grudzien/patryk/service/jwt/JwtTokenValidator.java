package me.grudzien.patryk.service.jwt;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZonedDateTime;

public interface JwtTokenValidator {

    boolean isAccessTokenExpired(String accessToken);

    boolean isCreatedBeforeLastPasswordReset(ZonedDateTime created, ZonedDateTime lastPasswordReset);

    <T extends UserDetails> boolean validateAccessToken(String accessToken, T userDetails);
}
