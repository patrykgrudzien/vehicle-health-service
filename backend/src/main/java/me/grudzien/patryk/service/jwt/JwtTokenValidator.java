package me.grudzien.patryk.service.jwt;

import io.jsonwebtoken.ExpiredJwtException;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZonedDateTime;

public interface JwtTokenValidator {

    boolean isAccessTokenExpired(String accessToken);

    boolean isCreatedBeforeLastPasswordReset(ZonedDateTime created, ZonedDateTime lastPasswordReset);

    <T extends UserDetails> boolean isAccessTokenValid(String accessToken, T userDetails) throws ExpiredJwtException;
}
