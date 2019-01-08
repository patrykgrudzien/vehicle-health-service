package me.grudzien.patryk.service.jwt;

import io.jsonwebtoken.Claims;

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
}
