package me.grudzien.patryk.service.jwt.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import io.vavr.CheckedFunction1;
import io.vavr.control.Try;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
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
import me.grudzien.patryk.util.jwt.JwtTokenConstants;

import static me.grudzien.patryk.util.jwt.JwtTokenConstants.JWT_TOKEN_BEGIN_INDEX;

@Service
public class JwtTokenClaimsRetrieverImpl implements JwtTokenClaimsRetriever {

    private final PropertiesKeeper propertiesKeeper;

    private String tokenSecret;

    private DefaultClaims defaultClaims;
    private DefaultJwsHeader defaultJwsHeader;
    private String expiredJwtExceptionMessage;

    @Autowired
    public JwtTokenClaimsRetrieverImpl(final PropertiesKeeper propertiesKeeper) {
        Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
        this.propertiesKeeper = propertiesKeeper;
    }

    @PostConstruct
    public void init() {
        tokenSecret = propertiesKeeper.jwt().TOKEN_SECRET;
    }

    @Override
    public Optional<Claims> getAllClaimsFromToken(final String token) {
        final Try<Claims> tryParseAndGetBody = Try.of(() -> parseAndGetBodyFromToken(token));
        return tryParseAndGetBody.isSuccess() ?
                Optional.ofNullable(tryParseAndGetBody.get()) :
                Optional.ofNullable(
                        tryParseAndGetBody.onFailure(throwable -> {
                            if (ExpiredJwtException.class.isAssignableFrom(throwable.getClass())) {
                                final ExpiredJwtException exception = (ExpiredJwtException) throwable;
                                this.defaultClaims = (DefaultClaims) exception.getClaims();
                                this.defaultJwsHeader = (DefaultJwsHeader) exception.getHeader();
                                this.expiredJwtExceptionMessage = exception.getMessage();
                            }
                        }).get());  // get thrown exception (look into documentation for more details)
    }

    private Claims parseAndGetBodyFromToken(final String token) {
        return Jwts.parser()
                   .setSigningKey(tokenSecret)
                   .parseClaimsJws(token)
                   .getBody();
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
        /**
         * Recovering from {@link ExpiredJwtException} here is done by purpose because I want to allow
         * {@link me.grudzien.patryk.service.jwt.JwtTokenValidator#isAccessTokenExpired(String)} returning boolean value and then
         * pass that result to {@link me.grudzien.patryk.service.jwt.JwtTokenValidator#isAccessTokenValid(String, UserDetails)}.
         * Logic that will be responsible for throwing that "masked" exception will be placed inside
         * {@link me.grudzien.patryk.config.filters.GenericJwtTokenFilter} where {@link ExpiredJwtException} will be thrown and later caught by
         * {@link me.grudzien.patryk.config.filters.ServletExceptionHandlerFilter}.
         */
	    final Optional<Date> expirationOptional = CheckedFunction1.liftTry(t -> getClaimFromToken((String) t, claims -> Optional.ofNullable(claims.getExpiration())))
	                                                .apply(token)
	                                                .recover(ExpiredJwtException.class, Optional.empty())
	                                                .get();
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
            return Optional.of(Objects.requireNonNull(((WebRequest) request).getHeader(JwtTokenConstants.JWT_TOKEN_HEADER),
                                                      "NO \"Authorization\" header found!")
                                      .substring(JWT_TOKEN_BEGIN_INDEX));
        } else if (request instanceof HttpServletRequest) {
            return Optional.of(Objects.requireNonNull(((HttpServletRequest) request).getHeader(JwtTokenConstants.JWT_TOKEN_HEADER),
                                                      "NO \"Authorization\" header found!")
                                      .substring(JWT_TOKEN_BEGIN_INDEX));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Those methods below are needed in {@link me.grudzien.patryk.config.filters.GenericJwtTokenFilter} when (access_token) is expired
     * and {@link ExpiredJwtException} is thrown.
     */
    @Override
    public Optional<DefaultClaims> getDefaultClaims() {
        return Optional.ofNullable(defaultClaims);
    }

    @Override
    public Optional<DefaultJwsHeader> getDefaultJwsHeader() {
        return Optional.ofNullable(defaultJwsHeader);
    }

    @Override
    public Optional<String> getExpiredJwtExceptionMessage() {
        return Optional.ofNullable(expiredJwtExceptionMessage);
    }
}
