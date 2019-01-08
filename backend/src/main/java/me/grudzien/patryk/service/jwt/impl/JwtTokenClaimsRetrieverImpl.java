package me.grudzien.patryk.service.jwt.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

import com.google.common.base.Preconditions;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.domain.enums.ApplicationZone;
import me.grudzien.patryk.service.jwt.JwtTokenClaimsRetriever;

import static me.grudzien.patryk.util.jwt.JwtTokenConstants.JWT_TOKEN_BEGIN_INDEX;

@Service
public class JwtTokenClaimsRetrieverImpl implements JwtTokenClaimsRetriever {

    private final PropertiesKeeper propertiesKeeper;

    private String tokenHeader;
    private String tokenSecret;

    @Autowired
    public JwtTokenClaimsRetrieverImpl(final PropertiesKeeper propertiesKeeper) {
        Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
        this.propertiesKeeper = propertiesKeeper;
    }

    @PostConstruct
    public void init() {
        tokenHeader = propertiesKeeper.jwt().TOKEN_HEADER;
        tokenSecret = propertiesKeeper.jwt().TOKEN_SECRET;
    }

    @Override
    public Optional<Claims> getAllClaimsFromToken(final String token) {
        return Optional.ofNullable(Jwts.parser()
                                       .setSigningKey(tokenSecret)
                                       .parseClaimsJws(token)
                                       .getBody());
    }

    @Override
    public <T> Optional<T> getClaimFromToken(final String token, final Function<Claims, Optional<T>> claimsResolver) {
	    final Optional<Claims> claims = getAllClaimsFromToken(token);
	    return claims.isPresent() ? claimsResolver.apply(claims.get()) : Optional.empty();
    }

    @Override
    public Optional<String> getAudienceFromToken(final String token) {
    	return getClaimFromToken(token, claims -> Optional.of(claims.getAudience()));
    }

    @Override
    public Optional<ZonedDateTime> getExpirationDateFromToken(final String token) {
    	return getClaimFromToken(token, claims -> Optional.of(ZonedDateTime.ofInstant(claims.getExpiration().toInstant(),
	                                                                                  ZoneId.of(ApplicationZone.POLAND.getZoneId()))));
    }

    @Override
    public Optional<ZonedDateTime> getIssuedAtDateFromToken(final String token) {
        return getClaimFromToken(token, claims -> Optional.of(ZonedDateTime.ofInstant(claims.getIssuedAt().toInstant(),
                                                                                      ZoneId.of(ApplicationZone.POLAND.getZoneId()))));
    }

    @Override
    public Optional<String> getUserEmailFromToken(final String token) {
        return getClaimFromToken(token, claims -> Optional.of(claims.getSubject()));
    }

    @Override
    public <T> Optional<String> getJwtTokenFromRequest(final T request) {
        if (request instanceof WebRequest) {
            return Optional.of(Objects.requireNonNull(((WebRequest) request).getHeader(tokenHeader), "NO \"Authorization\" header found!")
                                      .substring(JWT_TOKEN_BEGIN_INDEX));
        } else if (request instanceof HttpServletRequest) {
            return Optional.of(Objects.requireNonNull(((HttpServletRequest) request).getHeader(tokenHeader), "NO \"Authorization\" header found!")
                                      .substring(JWT_TOKEN_BEGIN_INDEX));
        } else {
            return Optional.empty();
        }
    }
}
