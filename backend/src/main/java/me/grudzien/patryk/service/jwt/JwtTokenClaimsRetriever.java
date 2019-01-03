package me.grudzien.patryk.service.jwt;

import io.jsonwebtoken.Claims;

import java.time.ZonedDateTime;
import java.util.function.Function;

public interface JwtTokenClaimsRetriever {

    Claims getAllClaimsFromToken(String token);

    <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver);

    String getAudienceFromToken(String token);

    ZonedDateTime getExpirationDateFromToken(String token);

    ZonedDateTime getIssuedAtDateFromToken(String token);

    String getUserEmailFromToken(String token);

    <T> String getJwtTokenFromRequest(T request);
}
