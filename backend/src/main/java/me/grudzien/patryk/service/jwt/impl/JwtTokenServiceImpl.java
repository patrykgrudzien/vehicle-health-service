package me.grudzien.patryk.service.jwt.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import javax.annotation.PostConstruct;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.domain.enums.ApplicationZone;
import me.grudzien.patryk.domain.enums.security.JwtTokenClaims;
import me.grudzien.patryk.exception.security.NoEmailProvidedException;
import me.grudzien.patryk.exception.security.NoRefreshTokenProvidedException;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.util.OAuth2OidcAttributesExtractor;
import me.grudzien.patryk.service.jwt.JwtTokenClaimsRetriever;
import me.grudzien.patryk.service.jwt.JwtTokenService;
import me.grudzien.patryk.service.login.impl.MyUserDetailsService;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.util.jwt.JwtTokenConstants;
import me.grudzien.patryk.util.jwt.JwtTokenHelper;
import me.grudzien.patryk.util.web.RequestsDecoder;

import static me.grudzien.patryk.util.log.LogMarkers.METHOD_INVOCATION_MARKER;

@Log4j2
@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final UserDetailsService userDetailsService;
    private final LocaleMessagesCreator localeMessagesCreator;
    private final PropertiesKeeper propertiesKeeper;
    private final JwtTokenClaimsRetriever jwtTokenClaimsRetriever;
    private final RequestsDecoder requestsDecoder;

	private String tokenSecret;
	private Long accessTokenExpiration;
	private Long shortLivedTokenExpiration;

    @Autowired
    public JwtTokenServiceImpl(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
                               final LocaleMessagesCreator localeMessagesCreator, final PropertiesKeeper propertiesKeeper,
                               final JwtTokenClaimsRetriever jwtTokenClaimsRetriever, final RequestsDecoder requestsDecoder) {
        Preconditions.checkNotNull(userDetailsService, "userDetailsService cannot be null!");
        Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
        Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
        Preconditions.checkNotNull(jwtTokenClaimsRetriever, "jwtTokenClaimsRetriever cannot be null!");
        Preconditions.checkNotNull(requestsDecoder, "requestsDecoder cannot be null!");

        this.userDetailsService = userDetailsService;
        this.localeMessagesCreator = localeMessagesCreator;
        this.propertiesKeeper = propertiesKeeper;
        this.jwtTokenClaimsRetriever = jwtTokenClaimsRetriever;
        this.requestsDecoder = requestsDecoder;
    }

	@PostConstruct
	public void init() {
    	tokenSecret = propertiesKeeper.jwt().TOKEN_SECRET;
    	accessTokenExpiration = propertiesKeeper.jwt().ACCESS_TOKEN_EXPIRATION;
    	shortLivedTokenExpiration = propertiesKeeper.oAuth2().SHORT_LIVED_TOKEN_EXPIRATION;
	}

	@SuppressWarnings("Duplicates")
	@Override
	public String generateAccessToken(final JwtAuthenticationRequest authenticationRequest, final Device device) {
		log.info(METHOD_INVOCATION_MARKER, "(JWT) -----> {}.{}", this.getClass().getCanonicalName(), "generateAccessToken()");
		final String decodedEmail = requestsDecoder.decodeStringParam(authenticationRequest.getEmail());
        return Optional.ofNullable(decodedEmail)
                       .map(email -> Optional.ofNullable(((JwtUser) userDetailsService.loadUserByUsername(email)))
                                             .map(jwtUser -> buildAccessToken(jwtUser, device, accessTokenExpiration))
                                             .orElseThrow(() -> new UsernameNotFoundException(localeMessagesCreator.buildLocaleMessageWithParam(
                                                     "user-not-found-by-email", email))))
                       .orElseThrow(() -> new NoEmailProvidedException(localeMessagesCreator.buildLocaleMessageWithParam(
                       		"no-email-provided-in-auth-request", JwtTokenConstants.TokenType.ACCESS_TOKEN
                       )));
    }

	@SuppressWarnings("Duplicates")
    @Override
	public String generateAccessTokenCustomExpiration(final JwtAuthenticationRequest authenticationRequest, final Device device, final long minutesToExpire) {
        log.info(METHOD_INVOCATION_MARKER, "(JWT) -----> {}.{}", this.getClass().getCanonicalName(), "generateAccessTokenCustomExpiration()");
        final String decodedEmail = requestsDecoder.decodeStringParam(authenticationRequest.getEmail());
        return Optional.ofNullable(decodedEmail)
                       .map(email -> Optional.ofNullable(((JwtUser) userDetailsService.loadUserByUsername(email)))
                                             .map(jwtUser -> buildAccessToken(jwtUser, device, minutesToExpire))
                                             .orElseThrow(() -> new UsernameNotFoundException(localeMessagesCreator.buildLocaleMessageWithParam(
                                                     "user-not-found-by-email", email))))
                       .orElseThrow(() -> new NoEmailProvidedException(localeMessagesCreator.buildLocaleMessageWithParam(
		                       "no-email-provided-in-auth-request", JwtTokenConstants.TokenType.ACCESS_TOKEN
                       )));
	}

	@Override
    public String generateRefreshToken(final JwtAuthenticationRequest authenticationRequest, final Device device) {
        log.info(METHOD_INVOCATION_MARKER, "(JWT) -----> {}.{}", this.getClass().getCanonicalName(), "generateRefreshToken()");
        final String decodedEmail = requestsDecoder.decodeStringParam(authenticationRequest.getEmail());
        return Optional.ofNullable(decodedEmail)
                       .map(email -> Optional.ofNullable(((JwtUser) userDetailsService.loadUserByUsername(email)))
                                             .map(jwtUser -> {
                                                 log.info("Started generating refresh token...");
                                                 return Jwts.builder()
                                                            .setSubject(jwtUser.getEmail())
                                                            .signWith(SignatureAlgorithm.HS512, tokenSecret)
                                                            .compact();
                                             })
                                             .orElseThrow(() -> new UsernameNotFoundException(localeMessagesCreator.buildLocaleMessageWithParam(
                                                     "user-not-found-by-email", email))))
                       .orElseThrow(() -> new NoEmailProvidedException(localeMessagesCreator.buildLocaleMessageWithParam(
		                       "no-email-provided-in-auth-request", JwtTokenConstants.TokenType.REFRESH_TOKEN
                       )));
    }

    @Override
    public String refreshAccessToken(final JwtAuthenticationRequest authenticationRequest, final Device device) {
        log.info(METHOD_INVOCATION_MARKER, "(JWT) -----> {}.{}", this.getClass().getCanonicalName(), "refreshAccessToken()");
        return Optional.ofNullable(authenticationRequest.getRefreshToken())
                       .map(refreshToken -> {
                           final String email = jwtTokenClaimsRetriever.getUserEmailFromToken(refreshToken).orElse(null);
                           return Optional.ofNullable(((JwtUser) userDetailsService.loadUserByUsername(email)))
                                          .map(jwtUser -> buildAccessToken(jwtUser, device, accessTokenExpiration))
                                          .orElseThrow(() -> new UsernameNotFoundException(localeMessagesCreator.buildLocaleMessageWithParam(
                                                  "user-not-found-by-email", email)));
                       })
                       .orElseThrow(() -> new NoRefreshTokenProvidedException(
                               localeMessagesCreator.buildLocaleMessage("no-refresh-token-provided")));
    }

    @Override
    public String generateShortLivedToken(final CustomOAuth2OidcPrincipalUser customOAuth2OidcPrincipalUser) {
        log.info(METHOD_INVOCATION_MARKER, "(JWT) -----> {}.{}", this.getClass().getCanonicalName(), "generateShortLivedToken()");
        return Optional.ofNullable(customOAuth2OidcPrincipalUser)
                       .map(principalUser -> {
                           final Map<String, Object> principalUserAttributes = customOAuth2OidcPrincipalUser.getAttributes();
                           final String oAuth2Email = OAuth2OidcAttributesExtractor.getOAuth2AttributeValue(principalUserAttributes, StandardClaimNames.EMAIL);
                           final Map<String, Object> additionalClaims = new HashMap<>();
                           additionalClaims.put(StandardClaimNames.PICTURE, OAuth2OidcAttributesExtractor.getOAuth2AttributeValue(principalUserAttributes,
                                                                                                                                  StandardClaimNames.PICTURE));
                           return buildShortLivedToken(oAuth2Email, additionalClaims);
                       })
                       .orElseThrow(() -> new RuntimeException("Cannot generate short lived token as passed OAuth2/OpenID Connect user is null!"));
    }

    private <T extends UserDetails> String buildAccessToken(final T userDetails, final Device device, final long minutesToExpire) {
        log.info("Started generating access token...");
        final Map<String, Object> additionalClaims = new HashMap<>();
        final JwtUser jwtUser = (JwtUser) userDetails;
        additionalClaims.put(JwtTokenClaims.USER_ROLES.getKey(), jwtUser.getAuthorities());
        return Jwts.builder()
                   .setClaims(additionalClaims)
                   .setSubject(jwtUser.getEmail())
                   .setAudience(JwtTokenHelper.generateAudience(device))
                   .setIssuedAt(Date.from(ApplicationZone.POLAND.getApplicationZonedDateTimeNow().toInstant()))
                   .setExpiration(Date.from(ApplicationZone.POLAND.getApplicationZonedDateTimeNow()
                                                                  .toLocalDateTime()
                                                                  .plusMinutes(minutesToExpire)
                                                                  .toInstant(ZoneId.of(ApplicationZone.POLAND.getZoneId())
                                                                                   .getRules()
                                                                                   .getOffset(ApplicationZone.POLAND.getApplicationZonedDateTimeNow().toLocalDateTime()))))
                   .signWith(SignatureAlgorithm.HS512, tokenSecret)
                   .compact();
    }

    private String buildShortLivedToken(final String oAuth2Email, final Map<String, Object> additionalClaims) {
        log.info("Started generating short lived token...");
        return Jwts.builder()
                   .setClaims(additionalClaims)
                   .setSubject(oAuth2Email)
                   .setIssuedAt(Date.from(ApplicationZone.POLAND.getApplicationZonedDateTimeNow().toInstant()))
                   .setExpiration(Date.from(ApplicationZone.POLAND.getApplicationZonedDateTimeNow()
                                                                  .toLocalDateTime()
                                                                  .plusMinutes(shortLivedTokenExpiration)
                                                                  .toInstant(ZoneId.of(ApplicationZone.POLAND.getZoneId())
                                                                                   .getRules()
                                                                                   .getOffset(ApplicationZone.POLAND.getApplicationZonedDateTimeNow().toLocalDateTime()))))
                   .signWith(SignatureAlgorithm.HS512, tokenSecret)
                   .compact();
    }
}
