package me.grudzien.patryk.integration.service.jwt;

import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.TestsUtils;
import me.grudzien.patryk.domain.enums.ApplicationZone;
import me.grudzien.patryk.domain.enums.security.JwtTokenClaims;
import me.grudzien.patryk.service.jwt.JwtTokenClaimsRetriever;
import me.grudzien.patryk.service.jwt.JwtTokenService;
import me.grudzien.patryk.util.jwt.JwtTokenConstants;

import static org.assertj.core.api.Assertions.assertThat;

import static me.grudzien.patryk.TestsUtils.TEST_EMAIL;

@Disabled("Disabled because of: net.sf.ehcache.CacheException: Another unnamed CacheManager already exists in the same VM.")
@SpringBootTest
class JwtTokenClaimsRetrieverImplIT {

    @Autowired
    private JwtTokenClaimsRetriever jwtTokenClaimsRetriever;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private PropertiesKeeper propertiesKeeper;

    private String tokenHeader;
    private String accessToken;

    private static final LocalTime NOW_IN_POLAND = LocalTime.now(ZoneId.of(ApplicationZone.POLAND.getZoneId()));

    @BeforeEach
    void setUp() {
        tokenHeader = propertiesKeeper.jwt().TOKEN_HEADER;
        accessToken = TestsUtils.prepareTestAccessToken(jwtTokenService);
    }

    @AfterEach
    void tearDown() {
        accessToken = null;
    }

    @Test
    void getAllClaimsFromToken() {
        // when
	    final Optional<Claims> allClaimsOptional = jwtTokenClaimsRetriever.getAllClaimsFromToken(accessToken);
	    final Claims allClaims = allClaimsOptional.orElseThrow(() -> new RuntimeException("No claims found inside access token!"));

	    final LocalDateTime issuedAt = LocalDateTime.ofInstant(allClaims.getIssuedAt().toInstant(), ZoneId.of(ApplicationZone.POLAND.getZoneId()));
        final LocalDateTime expiration = LocalDateTime.ofInstant(allClaims.getExpiration().toInstant(), ZoneId.of(ApplicationZone.POLAND.getZoneId()));

        // then
        Assertions.assertAll(
                () -> assertThat(allClaims).hasSize(5),
                () -> assertThat(allClaims.get(JwtTokenClaims.USER_ROLES.getKey())).isNotNull(),
                () -> assertThat(allClaims.getSubject()).isEqualTo(TEST_EMAIL),
                () -> assertThat(allClaims.getAudience()).isEqualTo("web"),
                () -> assertThat(issuedAt.toLocalTime().getHour()).isEqualTo(NOW_IN_POLAND.getHour()),
                () -> assertThat(issuedAt.toLocalTime().getMinute()).isEqualTo(NOW_IN_POLAND.getMinute()),
                () -> assertThat(issuedAt.toLocalDate()).isEqualTo(LocalDate.now()),
                () -> assertThat(expiration.toLocalTime().getHour()).isEqualTo(NOW_IN_POLAND.plusMinutes(15).getHour()),
                () -> assertThat(expiration.toLocalTime().getMinute()).isEqualTo(NOW_IN_POLAND.plusMinutes(15).getMinute()),
                () -> assertThat(expiration.toLocalDate()).isEqualTo(LocalDate.now())
        );
    }

    @Test
    void getClaimFromToken() {
        // when
	    final Optional<String> subjectOptional = jwtTokenClaimsRetriever.getClaimFromToken(accessToken, claims -> Optional.of(claims.getSubject()));
	    final String subject = subjectOptional.orElseThrow(() -> new RuntimeException("No subject claim found inside access token!"));

        // then
        assertThat(subject).isEqualTo(TEST_EMAIL);
    }

    @Test
    void getAudienceFromToken() {
        // when
	    final Optional<String> audienceOptional = jwtTokenClaimsRetriever.getAudienceFromToken(accessToken);
	    final String audience = audienceOptional.orElseThrow(() -> new RuntimeException("No audience claim found inside access token!"));

	    // then
        assertThat(audience).isEqualTo("web");
    }

    @Test
    void getExpirationDateFromToken() {
        // when
	    final Optional<ZonedDateTime> expirationOptional = jwtTokenClaimsRetriever.getExpirationDateFromToken(accessToken);
	    final ZonedDateTime expiration = expirationOptional.orElseThrow(() -> new RuntimeException("No expiration claim found inside access token!"));

	    // then
        Assertions.assertAll(
                () -> assertThat(expiration.toLocalTime().getHour()).isEqualTo(NOW_IN_POLAND.plusMinutes(15).getHour()),
                () -> assertThat(expiration.toLocalTime().getMinute()).isEqualTo(NOW_IN_POLAND.plusMinutes(15).getMinute()),
                () -> assertThat(expiration.toLocalDate()).isEqualTo(LocalDate.now())
        );
    }

    @Test
    void getIssuedAtDateFromToken() {
        // when
	    final Optional<ZonedDateTime> issuedAtOptional = jwtTokenClaimsRetriever.getIssuedAtDateFromToken(accessToken);
	    final ZonedDateTime issuedAt = issuedAtOptional.orElseThrow(() -> new RuntimeException("No issuedAt claim found inside access token!"));

	    // then
        Assertions.assertAll(
                () -> assertThat(issuedAt.toLocalTime().getHour()).isEqualTo(NOW_IN_POLAND.getHour()),
                () -> assertThat(issuedAt.toLocalTime().getMinute()).isEqualTo(NOW_IN_POLAND.getMinute()),
                () -> assertThat(issuedAt.toLocalDate()).isEqualTo(LocalDate.now())
        );
    }

    @Test
    void getUserEmailFromToken() {
        // when
	    final Optional<String> userEmailOptional = jwtTokenClaimsRetriever.getUserEmailFromToken(accessToken);
	    final String userEmail = userEmailOptional.orElseThrow(() -> new RuntimeException("No user email claim found inside access token!"));

	    // then
        assertThat(userEmail).isEqualTo(TEST_EMAIL);
    }

    @Test
    void getJwtTokenFromRequest() {
        // given
        final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader(tokenHeader, JwtTokenConstants.BEARER + accessToken);

        // when
	    final Optional<String> jwtTokenOptional = jwtTokenClaimsRetriever.getJwtTokenFromRequest(mockRequest);
	    final String jwtToken = jwtTokenOptional.orElseThrow(() -> new RuntimeException("No jwtToken found inside request!"));

	    // then
        assertThat(jwtToken).isEqualTo(accessToken);
    }
}