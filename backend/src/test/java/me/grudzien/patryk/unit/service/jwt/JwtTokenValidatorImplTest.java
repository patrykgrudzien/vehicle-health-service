package me.grudzien.patryk.unit.service.jwt;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.domain.enums.ApplicationZone;
import me.grudzien.patryk.service.jwt.JwtTokenClaimsRetriever;
import me.grudzien.patryk.service.jwt.JwtTokenValidator;
import me.grudzien.patryk.service.jwt.impl.JwtTokenValidatorImpl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static me.grudzien.patryk.TestsUtils.TEST_EMAIL;
import static me.grudzien.patryk.TestsUtils.prepareTestJwtUser;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
class JwtTokenValidatorImplTest {

	@MockBean
	private JwtTokenClaimsRetriever jwtTokenClaimsRetriever;

	private JwtTokenValidator jwtTokenValidator;

	private String randomAccessToken;
	private JwtUser userWithLastPasswordResetDate7DaysBack;

	@BeforeEach
	void setUp() {
		jwtTokenValidator = new JwtTokenValidatorImpl(jwtTokenClaimsRetriever);
		randomAccessToken = RandomStringUtils.randomAlphanumeric(25);
		userWithLastPasswordResetDate7DaysBack = prepareTestJwtUser(ApplicationZone.POLAND.now().minusDays(7L));
	}

    @AfterEach
    void tearDown() {
        randomAccessToken = null;
        userWithLastPasswordResetDate7DaysBack = null;
    }

    @Test
	void isAccessTokenExpired_true_adjustTimeToApplicationZoneOffset() {
		// given
		final ZonedDateTime pacificZoneDateTime = ZonedDateTime.now(ZoneId.of("Pacific/Majuro"));
		BDDMockito.given(jwtTokenClaimsRetriever.getExpirationDateFromToken(anyString()))
		          .willReturn(Optional.of(pacificZoneDateTime.minusSeconds(1L)));
		// when
		final boolean isAccessTokenExpired = jwtTokenValidator.isAccessTokenExpired(randomAccessToken);

		// then
		assertTrue(isAccessTokenExpired);
        verify(jwtTokenClaimsRetriever, times(1)).getExpirationDateFromToken(anyString());
	}

	@Test
	void isAccessTokenExpired_true_noExpirationClaimFound() {
		// given
		BDDMockito.given(jwtTokenClaimsRetriever.getExpirationDateFromToken(anyString()))
		          .willReturn(Optional.empty());
		// when
		final boolean isAccessTokenExpired = jwtTokenValidator.isAccessTokenExpired(randomAccessToken);

		// then
		assertTrue(isAccessTokenExpired);
        verify(jwtTokenClaimsRetriever, times(1)).getExpirationDateFromToken(anyString());
	}

	@Test
	void isCreatedBeforeLastPasswordReset() {
		// given
		final ZonedDateTime created7DaysAgo = ApplicationZone.POLAND.now().minusDays(7L);
		final ZonedDateTime passwordResetToday = ApplicationZone.POLAND.now();

		// when
		final boolean isCreatedBeforeLastPasswordReset = jwtTokenValidator.isCreatedBeforeLastPasswordReset(created7DaysAgo, passwordResetToday);

		// then
		assertTrue(isCreatedBeforeLastPasswordReset);
	}

    @Test
    void isAccessTokenValid_true() {
        // given
        emailInTokenExists();
        issuedAtNow();
        expirationIn15Minutes();

        // when
        final boolean isAccessTokenValid = jwtTokenValidator.isAccessTokenValid(randomAccessToken, userWithLastPasswordResetDate7DaysBack);

        // then
        assertTrue(isAccessTokenValid);
        verify(jwtTokenClaimsRetriever, times(1)).getUserEmailFromToken(anyString());
        verify(jwtTokenClaimsRetriever, times(1)).getIssuedAtDateFromToken(anyString());
        verify(jwtTokenClaimsRetriever, times(1)).getExpirationDateFromToken(anyString());
    }

    @Test
    void isAccessTokenValid_false_emailInTokenNotExists() {
        // given
        emailInTokenEmpty();
        issuedAtNow();
        expirationIn15Minutes();

        // when
        final boolean isAccessTokenValid = jwtTokenValidator.isAccessTokenValid(randomAccessToken, userWithLastPasswordResetDate7DaysBack);

        // then
        assertFalse(isAccessTokenValid);
        verify(jwtTokenClaimsRetriever, times(1)).getUserEmailFromToken(anyString());
        verify(jwtTokenClaimsRetriever, times(1)).getIssuedAtDateFromToken(anyString());
        verify(jwtTokenClaimsRetriever, times(0)).getExpirationDateFromToken(anyString());
    }

    @Test
    void isAccessTokenValid_false_tokenExpired() {
        // given
        emailInTokenExists();
        issuedAtNow();
        tokenExpired();

        // when
        final boolean isAccessTokenValid = jwtTokenValidator.isAccessTokenValid(randomAccessToken, userWithLastPasswordResetDate7DaysBack);

        // then
        assertFalse(isAccessTokenValid);
        verify(jwtTokenClaimsRetriever, times(1)).getUserEmailFromToken(anyString());
        verify(jwtTokenClaimsRetriever, times(1)).getIssuedAtDateFromToken(anyString());
        verify(jwtTokenClaimsRetriever, times(1)).getExpirationDateFromToken(anyString());
    }

    @Test
    void isAccessTokenValid_false_issuedAtEmpty() {
        // given
        emailInTokenExists();
        issuedAtEmpty();
        expirationIn15Minutes();

        // when
        final boolean isAccessTokenValid = jwtTokenValidator.isAccessTokenValid(randomAccessToken, userWithLastPasswordResetDate7DaysBack);

        // then
        assertFalse(isAccessTokenValid);
        verify(jwtTokenClaimsRetriever, times(1)).getUserEmailFromToken(anyString());
        verify(jwtTokenClaimsRetriever, times(1)).getIssuedAtDateFromToken(anyString());
        verify(jwtTokenClaimsRetriever, times(0)).getExpirationDateFromToken(anyString());
    }

    private void emailInTokenExists() {
        BDDMockito.given(jwtTokenClaimsRetriever.getUserEmailFromToken(anyString()))
                  .willReturn(Optional.of(TEST_EMAIL));
    }

    private void emailInTokenEmpty() {
        BDDMockito.given(jwtTokenClaimsRetriever.getUserEmailFromToken(anyString()))
                  .willReturn(Optional.empty());
    }

    private void issuedAtNow() {
        BDDMockito.given(jwtTokenClaimsRetriever.getIssuedAtDateFromToken(anyString()))
                  .willReturn(Optional.of(ApplicationZone.POLAND.now()));
    }

    private void issuedAtEmpty() {
        BDDMockito.given(jwtTokenClaimsRetriever.getIssuedAtDateFromToken(anyString()))
                  .willReturn(Optional.empty());
    }

    private void expirationIn15Minutes() {
        BDDMockito.given(jwtTokenClaimsRetriever.getExpirationDateFromToken(anyString()))
                  .willReturn(Optional.of(ApplicationZone.POLAND.now().plusMinutes(15L)));
    }

    private void tokenExpired() {
        BDDMockito.given(jwtTokenClaimsRetriever.getExpirationDateFromToken(anyString()))
                  .willReturn(Optional.of(ApplicationZone.POLAND.now().minusMinutes(1L)));
    }
}
