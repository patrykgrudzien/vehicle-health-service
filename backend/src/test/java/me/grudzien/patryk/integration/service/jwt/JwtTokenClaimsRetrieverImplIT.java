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

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.TestsUtils;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.enums.ApplicationZone;
import me.grudzien.patryk.domain.enums.security.JwtTokenClaims;
import me.grudzien.patryk.service.jwt.JwtTokenClaimsRetriever;
import me.grudzien.patryk.service.jwt.JwtTokenService;
import me.grudzien.patryk.util.jwt.JwtTokenConstants;

import static org.assertj.core.api.Assertions.assertThat;

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

    private static final String TEST_EMAIL = "admin.root@gmail.com";
    private static final LocalTime NOW_IN_POLAND = LocalTime.now(ZoneId.of(ApplicationZone.POLAND.getZoneId()));

    @BeforeEach
    void setUp() {
        tokenHeader = propertiesKeeper.jwt().TOKEN_HEADER;
        accessToken = jwtTokenService.generateAccessToken(JwtAuthenticationRequest.Builder()
                                                                                  .email(TEST_EMAIL)
                                                                                  .password("password")
                                                                                  .build(),
                                                          TestsUtils.testDevice());
    }

    @AfterEach
    void tearDown() {
        accessToken = null;
    }

    @Test
    void getAllClaimsFromToken() {
        // when
        final Claims allClaims = jwtTokenClaimsRetriever.getAllClaimsFromToken(accessToken);

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
        final String subject = jwtTokenClaimsRetriever.getClaimFromToken(accessToken, Claims::getSubject);

        // then
        assertThat(subject).isEqualTo(TEST_EMAIL);
    }

    @Test
    void getAudienceFromToken() {
        // when
        final String audience = jwtTokenClaimsRetriever.getAudienceFromToken(accessToken);

        // then
        assertThat(audience).isEqualTo("web");
    }

    @Test
    void getExpirationDateFromToken() {
        // when
        final ZonedDateTime expiration = jwtTokenClaimsRetriever.getExpirationDateFromToken(accessToken);

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
        final ZonedDateTime issuedAt = jwtTokenClaimsRetriever.getIssuedAtDateFromToken(accessToken);

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
        final String userEmail = jwtTokenClaimsRetriever.getUserEmailFromToken(accessToken);

        // then
        assertThat(userEmail).isEqualTo(TEST_EMAIL);
    }

    @Test
    void getJwtTokenFromRequest() {
        // given
        final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader(tokenHeader, JwtTokenConstants.BEARER + accessToken);

        // when
        final String jwtToken = jwtTokenClaimsRetriever.getJwtTokenFromRequest(mockRequest);

        // then
        assertThat(jwtToken).isEqualTo(accessToken);
    }
}