package me.grudzien.patryk.service.jwt.impl;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.google.common.base.Preconditions;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.domain.enums.ApplicationZone;
import me.grudzien.patryk.service.jwt.JwtTokenClaimsRetriever;
import me.grudzien.patryk.service.jwt.JwtTokenValidator;
import me.grudzien.patryk.util.date.DateOperationsHelper;
import me.grudzien.patryk.util.log.LogMarkers;

import static me.grudzien.patryk.util.log.LogMarkers.METHOD_INVOCATION_MARKER;

@Log4j2
@Component
public class JwtTokenValidatorImpl implements JwtTokenValidator {

    private final JwtTokenClaimsRetriever jwtTokenClaimsRetriever;

    private final DateOperationsHelper dateOperationsHelper = new DateOperationsHelper();

    @Autowired
    public JwtTokenValidatorImpl(final JwtTokenClaimsRetriever jwtTokenClaimsRetriever) {
        Preconditions.checkNotNull(jwtTokenClaimsRetriever, "jwtTokenClaimsRetriever cannot be null!");
        this.jwtTokenClaimsRetriever = jwtTokenClaimsRetriever;
    }

    @Override
    public boolean isAccessTokenExpired(final String accessToken) {
        log.info(METHOD_INVOCATION_MARKER, "(JWT) -----> {}.{}", this.getClass().getCanonicalName(), "isAccessTokenExpired()");

	    final Optional<ZonedDateTime> expirationZonedDateTime = jwtTokenClaimsRetriever.getExpirationDateFromToken(accessToken);
	    final ZonedDateTime applicationZonedDateTimeNow = ApplicationZone.POLAND.now();

	    return expirationZonedDateTime.map(expiration -> {
	    	log.info(LogMarkers.SECURITY_MARKER, "Expiration claim successfully found in the token.");
		    if (expiration.getOffset() != applicationZonedDateTimeNow.getOffset()) {
			    log.info(METHOD_INVOCATION_MARKER, "Token created in a different time zone than {}.", ApplicationZone.POLAND.getZoneId());
			    final LocalDateTime adjustedExpiration = dateOperationsHelper.adjustTimeToApplicationZoneOffset(expiration);
			    return adjustedExpiration.toLocalTime().isBefore(applicationZonedDateTimeNow.toLocalTime());
		    }
		    return expiration.isBefore(applicationZonedDateTimeNow);
	    }).orElseGet(() -> {
		    log.info(LogMarkers.SECURITY_MARKER, "Expiration claim has NOT been found in the token!");
		    return Boolean.TRUE;
	    });
    }

    @Override
    public boolean isCreatedBeforeLastPasswordReset(final ZonedDateTime created, final ZonedDateTime lastPasswordReset) {
    	return (created != null && lastPasswordReset != null && created.isBefore(lastPasswordReset));
    }

    @Override
    public <T extends UserDetails> boolean isAccessTokenValid(final String accessToken, final T userDetails) throws ExpiredJwtException {
	    final JwtUser user = (JwtUser) userDetails;
	    final String emailFromToken = jwtTokenClaimsRetriever.getUserEmailFromToken(accessToken).orElse("");
	    final ZonedDateTime issuedAtDateFromToken = jwtTokenClaimsRetriever.getIssuedAtDateFromToken(accessToken).orElse(null);

	    return (isNotEmpty(Stream.of(user, emailFromToken, issuedAtDateFromToken))) &&
                (user.getEmail().equals(emailFromToken) &&
                        !isAccessTokenExpired(accessToken) &&
                        !isCreatedBeforeLastPasswordReset(issuedAtDateFromToken, user.getLastPasswordResetDate())
                );
    }

	private <T> boolean isNotEmpty(final Stream<T> streamOfObjects) {
    	return streamOfObjects.noneMatch(ObjectUtils::isEmpty);
	}
}
