package me.grudzien.patryk.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import com.google.common.base.Preconditions;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static me.grudzien.patryk.utils.log.LogMarkers.METHOD_INVOCATION_MARKER;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.dto.login.JwtUser;

@Log4j2
@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = -5153529441843568936L;

	/*
	 * The "sub" (subject) claim identifies the principal that is the subject of the JWT.
	 * The Claims in a JWT are normally statements about the subject. The subject value MAY be scoped to be locally unique in
	 * the context of the issuer or MAY be globally unique. The processing of this claim is generally application specific.
	 * Use of this claim is OPTIONAL.
	 */
	private static final String CLAIM_KEY_SUBJECT = "sub";

	/*
	 * The "aud" (audience) claim identifies the recipients that the JWT is intended for.
	 * Each principal intended to process the JWT MUST identify itself with a value in the audience claim.
	 * If the principal processing the claim does not identify itself with a value in the "aud" claim when this claim
	 * is present, then the JWT MUST be rejected.
	 */
	private static final String CLAIM_KEY_AUDIENCE = "aud";

	/*
	 * The "iat" (issued at) claim identifies the time at which the JWT was issued.
	 * This claim can be used to determine the age of the JWT. Its value MUST be a number containing an IntDate value.
	 * Use of this claim is OPTIONAL.
	 */
	private static final String CLAIM_KEY_CREATED = "iat";

	private static final String AUDIENCE_UNKNOWN = "unknown";
	private static final String AUDIENCE_WEB = "web";
	private static final String AUDIENCE_MOBILE = "mobile";
	private static final String AUDIENCE_TABLET = "tablet";

	public static final String BEARER = "Bearer ";
	public static final int JWT_TOKEN_BEGIN_INDEX = 7;

	private static String tokenSecret;
	private static String tokenHeader;
	private static Long tokenExpiration;
	private static Long expirationMillis;

	private final CustomApplicationProperties customApplicationProperties;
	private final PropertiesKeeper propertiesKeeper;

	@Autowired
	public JwtTokenUtil(final CustomApplicationProperties customApplicationProperties, final PropertiesKeeper propertiesKeeper) {
		Preconditions.checkNotNull(customApplicationProperties, "customApplicationProperties cannot be null!");
		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
		this.customApplicationProperties = customApplicationProperties;
		this.propertiesKeeper = propertiesKeeper;
	}

	@PostConstruct
	public void init() {
		tokenSecret = customApplicationProperties.getJwt().getSecret();
		tokenHeader = customApplicationProperties.getJwt().getHeader();
		tokenExpiration = customApplicationProperties.getJwt().getExpiration();
		expirationMillis = propertiesKeeper.oAuth2().SHORT_LIVED_MILLIS;
	}

	public static class Retriever {

		public static String getUserEmailFromToken(final String token) {
			return getClaimFromToken(token, Claims::getSubject);
		}

		public static Date getIssuedAtDateFromToken(final String token) {
			return getClaimFromToken(token, Claims::getIssuedAt);
		}

		public static Date getExpirationDateFromToken(final String token) {
			return getClaimFromToken(token, Claims::getExpiration);
		}

		@SuppressWarnings("unused")
		public static String getAudienceFromToken(final String token) {
			return getClaimFromToken(token, Claims::getAudience);
		}

		private static <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver) {
			final Claims claims = getAllClaimsFromToken(token);
			return claimsResolver.apply(claims);
		}

		private static Claims getAllClaimsFromToken(final String token) {
			return Jwts.parser()
			           .setSigningKey(tokenSecret)
			           .parseClaimsJws(token)
			           .getBody();
		}

		@SuppressWarnings("unused")
		public static <T> String getJwtTokenFromRequest(final T request) {
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

	public static class Creator {

		private enum TOKEN_CLAIMS {
			ROLES
		}

		public static String generateAccessToken(final JwtUser jwtUser, final Device device) {
			final Map<String, Object> claims = new HashMap<>();
			claims.put(TOKEN_CLAIMS.ROLES.name(), jwtUser.getRoles());
			return buildAccessToken(claims, jwtUser.getEmail(), generateAudience(device));
		}

		public static String generateRefreshToken(final JwtUser jwtUser) {
			return buildRefreshToken(jwtUser.getEmail());
		}

		private static String buildAccessToken(final Map<String, Object> claims, final String userEmail, final String audience) {
			final Date expirationDate = calculateExpirationDate(new Date());
			log.info(METHOD_INVOCATION_MARKER, "(JWT) -----> {}.{}", Creator.class.getCanonicalName(), "buildAccessToken()");
			return Jwts.builder()
			           .setClaims(claims)
			           .setSubject(userEmail)
			           .setAudience(audience)
			           .setIssuedAt(new Date())
			           .setExpiration(expirationDate)
			           .signWith(SignatureAlgorithm.HS512, tokenSecret)
			           .compact();
		}

		private static String buildRefreshToken(final String userEmail) {
			log.info(METHOD_INVOCATION_MARKER, "(JWT) -----> {}.{}", Creator.class.getCanonicalName(), "buildRefreshToken()");
			return Jwts.builder()
			           .setSubject(userEmail)
			           .signWith(SignatureAlgorithm.HS512, tokenSecret)
			           .compact();
		}

		private static Date calculateExpirationDate(final Date createdDate) {
			// token will be valid for 15 minutes (900 000 milliseconds)
			return new Date(createdDate.getTime() + tokenExpiration);
		}

		private static String generateAudience(final Device device) {
			String audience = AUDIENCE_UNKNOWN;
			if (device.isNormal()) {
				audience = AUDIENCE_WEB;
			} else if (device.isTablet()) {
				audience = AUDIENCE_TABLET;
			} else if (device.isMobile()) {
				audience = AUDIENCE_MOBILE;
			}
			return audience;
		}

		// TODO
		public static class OAuth2 {

			public static <T extends OAuth2User & OidcUser> String generateAccessToken(final T principal) {
				final Map<String, Object> claims = new HashMap<>();
				claims.put(TOKEN_CLAIMS.ROLES.name(), principal.getAuthorities());
				return buildAccessToken(claims, principal.getEmail(), expirationMillis);
			}

			private static String buildAccessToken(final Map<String, Object> claims, final String userEmail, final Long expirationMillis) {
				return Jwts.builder()
				           .setClaims(claims)
				           .setSubject(userEmail)
				           .setIssuedAt(new Date())
				           .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
				           .signWith(SignatureAlgorithm.HS512, tokenSecret)
				           .compact();
			}
		}
	}

	public static class Validator {

		private static Boolean isTokenExpired(final String token) {
			final Date expiration = Retriever.getExpirationDateFromToken(token);
			return expiration.before(new Date());
		}

		private static Boolean isCreatedBeforeLastPasswordReset(final Date created, final Date lastPasswordReset) {
			return (lastPasswordReset != null && created.before(lastPasswordReset));
		}

		public static Boolean validateToken(final String token, final UserDetails userDetails) {
			final JwtUser user = (JwtUser) userDetails;
			final String userEmail = Retriever.getUserEmailFromToken(token);
			final Date created = Retriever.getIssuedAtDateFromToken(token);
			return (userEmail.equals(user.getEmail()) && !isTokenExpired(token) && !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate()));
		}
	}
}
