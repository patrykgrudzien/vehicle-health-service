package me.grudzien.patryk.service.jwt.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.vavr.CheckedFunction1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

import com.google.common.base.Preconditions;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
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
	    final Optional<Claims> optionalClaims = getAllClaimsFromToken(token);
	    return CheckedFunction1.lift(claims -> claimsResolver.apply((Claims) claims))
	                           .apply(optionalClaims.orElseThrow(() -> new RuntimeException("No claims present inside token!")))
	                           // if there is no claims inside token -> return Optional.empty()
	                           .getOrElse(Optional.empty());
    }

    @Override
    public Optional<String> getAudienceFromToken(final String token) {
    	return getClaimFromToken(token, claims -> Optional.ofNullable(claims.getAudience()));
    }

    @Override
    public Optional<ZonedDateTime> getExpirationDateFromToken(final String token) {
	    final Optional<Date> expirationOptional = getClaimFromToken(token, claims -> Optional.ofNullable(claims.getExpiration()));
	    return expirationOptional.map(expiration -> ZonedDateTime.ofInstant(expiration.toInstant(), ZoneId.of(ApplicationZone.POLAND.getZoneId())));
    }

    @Override
    public Optional<ZonedDateTime> getIssuedAtDateFromToken(final String token) {
	    final Optional<Date> issuedAtOptional = getClaimFromToken(token, claims -> Optional.ofNullable(claims.getIssuedAt()));
	    return issuedAtOptional.map(issuedAt -> ZonedDateTime.ofInstant(issuedAt.toInstant(), ZoneId.of(ApplicationZone.POLAND.getZoneId())));
    }

    @Override
    public Optional<String> getUserEmailFromToken(final String token) {
        return getClaimFromToken(token, claims -> Optional.ofNullable(claims.getSubject()));
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
