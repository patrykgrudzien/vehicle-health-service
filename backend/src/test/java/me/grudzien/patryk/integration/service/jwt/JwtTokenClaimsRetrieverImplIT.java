package me.grudzien.patryk.integration.service.jwt;

import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.TestsUtils;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.enums.ApplicationZone;
import me.grudzien.patryk.domain.enums.security.JwtTokenClaims;
import me.grudzien.patryk.service.jwt.JwtTokenClaimsRetriever;
import me.grudzien.patryk.service.jwt.JwtTokenService;
import me.grudzien.patryk.util.date.DateOperationsHelper;
import me.grudzien.patryk.util.jwt.JwtTokenConstants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static me.grudzien.patryk.TestsUtils.TEST_EMAIL;
import static me.grudzien.patryk.TestsUtils.prepareAccessTokenRequest;
import static me.grudzien.patryk.TestsUtils.testDevice;

@SpringBootTest
@DirtiesContext
class JwtTokenClaimsRetrieverImplIT {

    @Autowired
    private JwtTokenClaimsRetriever jwtTokenClaimsRetriever;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private PropertiesKeeper propertiesKeeper;

    private String tokenHeader;
    private String accessToken;

    private JwtAuthenticationRequest tokenRequestOk;
    private String accessTokenExpired;

	private final DateOperationsHelper dateOperationsHelper = new DateOperationsHelper();

    private static final ZonedDateTime NOW_IN_POLAND = ApplicationZone.POLAND.now();

    @BeforeEach
    void setUp() {
        tokenHeader = propertiesKeeper.jwt().TOKEN_HEADER;
        accessToken = TestsUtils.prepareTestAccessToken(jwtTokenService);

        tokenRequestOk = prepareAccessTokenRequest(TEST_EMAIL, true);
        accessTokenExpired = jwtTokenService.generateAccessTokenCustomExpiration(tokenRequestOk, testDevice(), 0L);
    }

    @AfterEach
    void tearDown() {
        accessToken = null;

        tokenRequestOk = null;
        accessTokenExpired = null;
    }

    @Test
    void getAllClaimsFromToken() {
        // when
	    final Claims allClaims = jwtTokenClaimsRetriever.getAllClaimsFromToken(accessToken)
	                                                    .orElseThrow(() -> new RuntimeException("No claims found inside access token!"));
	    final ZonedDateTime issuedAt = ZonedDateTime.ofInstant(allClaims.getIssuedAt().toInstant(), ZoneId.of(ApplicationZone.POLAND.getZoneId()));
        final ZonedDateTime expiration = ZonedDateTime.ofInstant(allClaims.getExpiration().toInstant(), ZoneId.of(ApplicationZone.POLAND.getZoneId()));

        // then
        Assertions.assertAll(
                () -> assertThat(allClaims).hasSize(5),
                () -> assertThat(allClaims.get(JwtTokenClaims.USER_ROLES.getKey())).isNotNull(),
                () -> assertThat(allClaims.getSubject()).isEqualTo(TEST_EMAIL),
                () -> assertThat(allClaims.getAudience()).isEqualTo(JwtTokenConstants.AUDIENCE_WEB),
                () -> assertThat(dateOperationsHelper.getMinutesDifferenceBetween(issuedAt, NOW_IN_POLAND)).isEqualTo(0L),
                () -> assertThat(issuedAt.toLocalDate()).isEqualTo(NOW_IN_POLAND.toLocalDate()),
                () -> assertThat(dateOperationsHelper.getMinutesDifferenceBetween(expiration, issuedAt)).isEqualTo(15L),
                () -> assertThat(expiration.toLocalDate()).isEqualTo(NOW_IN_POLAND.toLocalDate())
        );
    }

    @Test
    void getClaimFromToken() {
        // when
	    final String subject = jwtTokenClaimsRetriever.getClaimFromToken(accessToken, claims -> Optional.ofNullable(claims.getSubject()))
	                                                  .orElseThrow(() -> new RuntimeException("No subject claim found inside access token!"));
        // then
        assertThat(subject).isEqualTo(TEST_EMAIL);
    }

	@Test
	void getClaimFromToken_whenNotExist() {
		// when
		final Optional<Date> notBeforeClaim = jwtTokenClaimsRetriever.getClaimFromToken(accessToken, claims -> Optional.ofNullable(claims.getNotBefore()));
		final Optional<String> issuer = jwtTokenClaimsRetriever.getClaimFromToken(accessToken, claims -> Optional.ofNullable(claims.getIssuer()));
		final Optional<String> id = jwtTokenClaimsRetriever.getClaimFromToken(accessToken, claims -> Optional.ofNullable(claims.getId()));

		// then
		Assertions.assertAll(
				() -> assertThat(notBeforeClaim).isEqualTo(Optional.empty()),
				() -> assertThat(issuer).isEqualTo(Optional.empty()),
				() -> assertThat(id).isEqualTo(Optional.empty())
		);
	}

	@Test
    void getAudienceFromToken() {
        // when
	    final String audience = jwtTokenClaimsRetriever.getAudienceFromToken(accessToken)
	                                                   .orElseThrow(() -> new RuntimeException("No audience claim found inside access token!"));
	    // then
        assertThat(audience).isEqualTo(JwtTokenConstants.AUDIENCE_WEB);
    }

	@Test
    void getExpirationDateFromToken() {
        // when
	    final ZonedDateTime expiration = jwtTokenClaimsRetriever.getExpirationDateFromToken(accessToken)
	                                                            .orElseThrow(() -> new RuntimeException("No expiration claim found inside access token!"));
		final ZonedDateTime issuedAt = jwtTokenClaimsRetriever.getIssuedAtDateFromToken(accessToken)
		                                                      .orElseThrow(() -> new RuntimeException("No expiration claim found inside access token!"));
	    // then
        Assertions.assertAll(
                () -> assertThat(dateOperationsHelper.getMinutesDifferenceBetween(expiration, issuedAt)).isEqualTo(15L),
                () -> assertThat(expiration.toLocalDate()).isEqualTo(NOW_IN_POLAND.toLocalDate())
        );
    }

    @Test
    void getExpirationDateFromToken_tokenExpired() {
        // when
        final Optional<ZonedDateTime> expiration = jwtTokenClaimsRetriever.getExpirationDateFromToken(accessTokenExpired);

        // then
        assertEquals(Optional.empty(), expiration);
    }

    @Test
    void getIssuedAtDateFromToken() {
        // when
	    final ZonedDateTime issuedAt = jwtTokenClaimsRetriever.getIssuedAtDateFromToken(accessToken)
	                                                          .orElseThrow(() -> new RuntimeException("No issuedAt claim found inside access token!"));
	    // then
        Assertions.assertAll(
                () -> assertThat(dateOperationsHelper.getMinutesDifferenceBetween(issuedAt, NOW_IN_POLAND)).isEqualTo(0L),
                () -> assertThat(issuedAt.toLocalDate()).isEqualTo(NOW_IN_POLAND.toLocalDate())
        );
    }

    @Test
    void getUserEmailFromToken() {
        // when
	    final String userEmail = jwtTokenClaimsRetriever.getUserEmailFromToken(accessToken)
	                                                    .orElseThrow(() -> new RuntimeException("No user email claim found inside access token!"));
	    // then
        assertThat(userEmail).isEqualTo(TEST_EMAIL);
    }

    @Test
    void getJwtTokenFromRequest() {
        // given
        final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader(tokenHeader, JwtTokenConstants.BEARER + accessToken);

        // when
	    final String jwtToken = jwtTokenClaimsRetriever.getJwtTokenFromRequest(mockRequest)
	                                                   .orElseThrow(() -> new RuntimeException("No jwtToken found inside request!"));
	    // then
        assertThat(jwtToken).isEqualTo(accessToken);
    }
}