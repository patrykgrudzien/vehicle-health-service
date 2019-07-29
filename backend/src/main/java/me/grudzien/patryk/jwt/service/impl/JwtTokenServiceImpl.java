package me.grudzien.patryk.jwt.service.impl;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtUser;
import me.grudzien.patryk.authentication.service.impl.MyUserDetailsService;
import me.grudzien.patryk.configuration.properties.jwt.CustomJwtProperties;
import me.grudzien.patryk.configuration.properties.oauth2.CustomOAuth2Properties;
import me.grudzien.patryk.jwt.exception.NoEmailProvidedException;
import me.grudzien.patryk.jwt.exception.NoRefreshTokenProvidedException;
import me.grudzien.patryk.jwt.model.enums.JwtTokenClaims;
import me.grudzien.patryk.jwt.service.JwtTokenClaimsRetriever;
import me.grudzien.patryk.jwt.service.JwtTokenService;
import me.grudzien.patryk.jwt.utils.JwtTokenConstants;
import me.grudzien.patryk.jwt.utils.JwtTokenHelper;
import me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.utils.OAuth2OidcAttributesExtractor;
import me.grudzien.patryk.utils.appplication.ApplicationZone;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.RequestsDecoder;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final UserDetailsService userDetailsService;
    private final LocaleMessagesCreator localeMessagesCreator;
    private final JwtTokenClaimsRetriever jwtTokenClaimsRetriever;
    private final RequestsDecoder requestsDecoder;
    private final CustomJwtProperties jwtProperties;
    private final CustomOAuth2Properties oAuth2Properties;

	private String tokenSecretKey;
	private Long tokenExpiration;
	private Long shortLivedTokenExpiration;

    @Autowired
    public JwtTokenServiceImpl(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
                               final LocaleMessagesCreator localeMessagesCreator, final JwtTokenClaimsRetriever jwtTokenClaimsRetriever,
                               final RequestsDecoder requestsDecoder, final CustomJwtProperties jwtProperties,
                               final CustomOAuth2Properties oAuth2Properties) {
        checkNotNull(userDetailsService, "userDetailsService cannot be null!");
        checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
        checkNotNull(jwtTokenClaimsRetriever, "jwtTokenClaimsRetriever cannot be null!");
        checkNotNull(requestsDecoder, "requestsDecoder cannot be null!");
        checkNotNull(oAuth2Properties, "oAuth2Properties cannot be null!");

        this.userDetailsService = userDetailsService;
        this.localeMessagesCreator = localeMessagesCreator;
        this.jwtTokenClaimsRetriever = jwtTokenClaimsRetriever;
        this.requestsDecoder = requestsDecoder;
        this.jwtProperties = jwtProperties;
        this.oAuth2Properties = oAuth2Properties;
    }

	@PostConstruct
	public void init() {
    	tokenSecretKey = jwtProperties.getTokenSecretKey();
    	tokenExpiration = jwtProperties.getTokenExpiration();
    	shortLivedTokenExpiration = oAuth2Properties.getShortLivedTokenExpiration();
	}

	@SuppressWarnings("Duplicates")
	@Override
	public String generateAccessToken(final JwtAuthenticationRequest authenticationRequest, final Device device) {
		log.info("(JWT) -----> {}.{}", this.getClass().getCanonicalName(), "generateAccessToken()");
		final String decodedEmail = requestsDecoder.decodeStringParam(authenticationRequest.getEmail());
        return Optional.ofNullable(decodedEmail)
                       .map(email -> Optional.ofNullable(((JwtUser) userDetailsService.loadUserByUsername(email)))
                                             .map(jwtUser -> buildAccessToken(jwtUser, device, tokenExpiration))
                                             .orElseThrow(() -> new UsernameNotFoundException(localeMessagesCreator.buildLocaleMessageWithParam(
                                                     "user-not-found-by-email", email))))
                       .orElseThrow(() -> new NoEmailProvidedException(localeMessagesCreator.buildLocaleMessageWithParam(
                       		"no-email-provided-in-auth-request", JwtTokenConstants.TokenType.ACCESS_TOKEN
                       )));
    }

	@SuppressWarnings("Duplicates")
    @Override
	public String generateAccessTokenCustomExpiration(final JwtAuthenticationRequest authenticationRequest, final Device device, final long minutesToExpire) {
        log.info("(JWT) -----> {}.{}", this.getClass().getCanonicalName(), "generateAccessTokenCustomExpiration()");
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
        log.info("(JWT) -----> {}.{}", this.getClass().getCanonicalName(), "generateRefreshToken()");
        final String decodedEmail = requestsDecoder.decodeStringParam(authenticationRequest.getEmail());
        return Optional.ofNullable(decodedEmail)
                       .map(email -> Optional.ofNullable(((JwtUser) userDetailsService.loadUserByUsername(email)))
                                             .map(jwtUser -> {
                                                 log.info("Started generating refresh token...");
                                                 return Jwts.builder()
                                                            .setSubject(jwtUser.getEmail())
                                                            .signWith(JwtTokenConstants.SIGNATURE_ALGORITHM, tokenSecretKey)
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
        log.info("(JWT) -----> {}.{}", this.getClass().getCanonicalName(), "refreshAccessToken()");
        return Optional.ofNullable(authenticationRequest.getRefreshToken())
                       .map(refreshToken -> {
                           final String email = jwtTokenClaimsRetriever.getUserEmailFromToken(refreshToken).orElse(null);
                           return Optional.ofNullable(((JwtUser) userDetailsService.loadUserByUsername(email)))
                                          .map(jwtUser -> buildAccessToken(jwtUser, device, tokenExpiration))
                                          .orElseThrow(() -> new UsernameNotFoundException(localeMessagesCreator.buildLocaleMessageWithParam(
                                                  "user-not-found-by-email", email)));
                       })
                       .orElseThrow(() -> new NoRefreshTokenProvidedException(
                               localeMessagesCreator.buildLocaleMessage("no-refresh-token-provided")));
    }

    @Override
    public String generateShortLivedToken(final CustomOAuth2OidcPrincipalUser customOAuth2OidcPrincipalUser) {
        log.info("(JWT) -----> {}.{}", this.getClass().getCanonicalName(), "generateShortLivedToken()");
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
                   .setIssuedAt(Date.from(ApplicationZone.POLAND.now().toInstant()))
                   .setExpiration(Date.from(ApplicationZone.POLAND.now()
                                                                  .toLocalDateTime()
                                                                  .plusMinutes(minutesToExpire)
                                                                  .toInstant(ZoneId.of(ApplicationZone.POLAND.getZoneId())
                                                                                   .getRules()
                                                                                   .getOffset(ApplicationZone.POLAND.now().toLocalDateTime()))))
                   .signWith(JwtTokenConstants.SIGNATURE_ALGORITHM, tokenSecretKey)
                   .compact();
    }

    private String buildShortLivedToken(final String oAuth2Email, final Map<String, Object> additionalClaims) {
        log.info("Started generating short lived token...");
        return Jwts.builder()
                   .setClaims(additionalClaims)
                   .setSubject(oAuth2Email)
                   .setIssuedAt(Date.from(ApplicationZone.POLAND.now().toInstant()))
                   .setExpiration(Date.from(ApplicationZone.POLAND.now()
                                                                  .toLocalDateTime()
                                                                  .plusMinutes(shortLivedTokenExpiration)
                                                                  .toInstant(ZoneId.of(ApplicationZone.POLAND.getZoneId())
                                                                                   .getRules()
                                                                                   .getOffset(ApplicationZone.POLAND.now().toLocalDateTime()))))
                   .signWith(JwtTokenConstants.SIGNATURE_ALGORITHM, tokenSecretKey)
                   .compact();
    }
}
