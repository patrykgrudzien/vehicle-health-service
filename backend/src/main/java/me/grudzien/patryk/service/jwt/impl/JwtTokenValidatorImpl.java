package me.grudzien.patryk.service.jwt.impl;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import me.grudzien.patryk.domain.enums.ApplicationZone;
import me.grudzien.patryk.service.jwt.JwtTokenClaimsRetriever;
import me.grudzien.patryk.service.jwt.JwtTokenValidator;
import me.grudzien.patryk.util.date.DateOperationsHelper;

import static me.grudzien.patryk.util.log.LogMarkers.METHOD_INVOCATION_MARKER;

@Log4j2
@Component
public class JwtTokenValidatorImpl implements JwtTokenValidator {

    private final JwtTokenClaimsRetriever jwtTokenClaimsRetriever;

    private static final DateOperationsHelper dateOperationsHelper = new DateOperationsHelper();

    @Autowired
    public JwtTokenValidatorImpl(final JwtTokenClaimsRetriever jwtTokenClaimsRetriever) {
        Preconditions.checkNotNull(jwtTokenClaimsRetriever, "jwtTokenClaimsRetriever cannot be null!");
        this.jwtTokenClaimsRetriever = jwtTokenClaimsRetriever;
    }

    @Override
    public boolean isAccessTokenExpired(final String accessToken) {
        log.info(METHOD_INVOCATION_MARKER, "(JWT) -----> {}.{}", this.getClass().getCanonicalName(), "isAccessTokenExpired()");
        final ZonedDateTime expirationZonedDateTime = jwtTokenClaimsRetriever.getExpirationDateFromToken(accessToken);
        final ZonedDateTime applicationZonedDateTimeNow = ApplicationZone.POLAND.getApplicationZonedDateTimeNow();

        if (expirationZonedDateTime.getOffset() != applicationZonedDateTimeNow.getOffset()) {
            log.info(METHOD_INVOCATION_MARKER, "Token created in a different time zone than {}.", ApplicationZone.POLAND.getZoneId());
            final LocalDateTime expirationLocalDateTime = dateOperationsHelper.adjustTimeToApplicationZone(expirationZonedDateTime);
            return expirationLocalDateTime.toLocalTime().isBefore(applicationZonedDateTimeNow.toLocalTime());
        }

        return expirationZonedDateTime.isBefore(applicationZonedDateTimeNow);
    }

    @Override
    public boolean isCreatedBeforeLastPasswordReset(final ZonedDateTime created, final ZonedDateTime lastPasswordReset) {
        return false;
    }

    @Override
    public <T extends UserDetails> boolean validateAccessToken(final String accessToken, final T userDetails) {
        return false;
    }
}
