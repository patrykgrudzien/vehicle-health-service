package me.grudzien.patryk.jwt.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.google.common.base.Preconditions;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import me.grudzien.patryk.authentication.model.dto.JwtUser;
import me.grudzien.patryk.jwt.service.JwtTokenClaimsRetriever;
import me.grudzien.patryk.jwt.service.JwtTokenValidator;
import me.grudzien.patryk.utils.appplication.ApplicationZone;
import me.grudzien.patryk.utils.date.DateOperationsHelper;

@Slf4j
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
        log.info("(JWT) -----> {}.{}", this.getClass().getCanonicalName(), "isAccessTokenExpired()");

	    final Optional<ZonedDateTime> expirationZonedDateTime = jwtTokenClaimsRetriever.getExpirationDateFromToken(accessToken);
	    final ZonedDateTime applicationZonedDateTimeNow = ApplicationZone.POLAND.now();

	    return expirationZonedDateTime.map(expiration -> {
	    	log.info("Expiration claim successfully found in the token.");
		    if (expiration.getOffset() != applicationZonedDateTimeNow.getOffset()) {
			    log.info("Token created in a different time zone than {}.", ApplicationZone.POLAND.getZoneId());
			    final LocalDateTime adjustedExpiration = dateOperationsHelper.adjustTimeToApplicationZoneOffset(expiration);
			    return adjustedExpiration.toLocalTime().isBefore(applicationZonedDateTimeNow.toLocalTime());
		    }
		    return expiration.isBefore(applicationZonedDateTimeNow);
	    }).orElseGet(() -> {
		    log.info("Expiration claim has NOT been found in the token!");
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
