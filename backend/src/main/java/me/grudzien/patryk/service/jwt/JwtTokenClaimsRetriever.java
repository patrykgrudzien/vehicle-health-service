package me.grudzien.patryk.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJwsHeader;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.Function;

public interface JwtTokenClaimsRetriever {

    Optional<Claims> getAllClaimsFromToken(String token);

    <T> Optional<T> getClaimFromToken(String token, Function<Claims, Optional<T>> claimsResolver);

    Optional<String> getAudienceFromToken(String token);

    Optional<ZonedDateTime> getExpirationDateFromToken(String token);

    Optional<ZonedDateTime> getIssuedAtDateFromToken(String token);

    Optional<String> getUserEmailFromToken(String token);

    <T> Optional<String> getJwtTokenFromRequest(T request);

    Optional<DefaultClaims> getDefaultClaims();

    Optional<DefaultJwsHeader> getDefaultJwsHeader();

    Optional<String> getExpiredJwtExceptionMessage();
}
