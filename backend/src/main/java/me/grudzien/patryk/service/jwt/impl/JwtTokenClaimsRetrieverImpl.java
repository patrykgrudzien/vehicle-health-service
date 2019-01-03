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
    public Claims getAllClaimsFromToken(final String token) {
        return Jwts.parser()
                   .setSigningKey(tokenSecret)
                   .parseClaimsJws(token)
                   .getBody();
    }

    @Override
    public <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String getAudienceFromToken(final String token) {
        return getClaimFromToken(token, Claims::getAudience);
    }

    @Override
    public ZonedDateTime getExpirationDateFromToken(final String token) {
        return getClaimFromToken(token, claim -> ZonedDateTime.ofInstant(claim.getExpiration().toInstant(),
                                                                         ZoneId.of(ApplicationZone.POLAND.getZoneId())));
    }

    @Override
    public ZonedDateTime getIssuedAtDateFromToken(final String token) {
        return getClaimFromToken(token, claim -> ZonedDateTime.ofInstant(claim.getIssuedAt().toInstant(),
                                                                         ZoneId.of(ApplicationZone.POLAND.getZoneId())));
    }

    @Override
    public String getUserEmailFromToken(final String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public <T> String getJwtTokenFromRequest(final T request) {
        if (request instanceof WebRequest) {
            return Objects.requireNonNull(((WebRequest) request).getHeader(tokenHeader), "NO \"Authorization\" header found!")
                          .substring(JWT_TOKEN_BEGIN_INDEX);
        } else if (request instanceof HttpServletRequest) {
            return Objects.requireNonNull(((HttpServletRequest) request).getHeader(tokenHeader), "NO \"Authorization\" header found!")
                          .substring(JWT_TOKEN_BEGIN_INDEX);
        } else {
            return null;
        }
    }
}
