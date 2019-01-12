package me.grudzien.patryk.integration.service.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.service.jwt.JwtTokenService;
import me.grudzien.patryk.service.jwt.JwtTokenValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static me.grudzien.patryk.TestsUtils.ENABLE_ENCODING;
import static me.grudzien.patryk.TestsUtils.TEST_EMAIL;
import static me.grudzien.patryk.TestsUtils.prepareAccessTokenRequest;
import static me.grudzien.patryk.TestsUtils.prepareTestJwtUser;
import static me.grudzien.patryk.TestsUtils.testDevice;

@Disabled("Disabled because of: net.sf.ehcache.CacheException: Another unnamed CacheManager already exists in the same VM.")
@SpringBootTest
class JwtTokenValidatorImplIT {

	@Autowired
	private JwtTokenValidator jwtTokenValidator;

	@Autowired
	private JwtTokenService jwtTokenService;

	private JwtAuthenticationRequest tokenRequestOk;

	@BeforeEach
	void setUp() {
		tokenRequestOk = prepareAccessTokenRequest(TEST_EMAIL, ENABLE_ENCODING);
	}

	@AfterEach
	void tearDown() {
		tokenRequestOk = null;
	}

    @Test
    void isAccessTokenExpired_false() {
    	// given
	    final String accessToken = jwtTokenService.generateAccessToken(tokenRequestOk, testDevice());

	    // when
	    final boolean isAccessTokenExpired = jwtTokenValidator.isAccessTokenExpired(accessToken);

	    // then
	    Assertions.assertAll(
			    () -> assertThat(accessToken).isNotNull(),
			    () -> assertThat(accessToken).isNotEmpty(),
			    () -> assertThat(accessToken).isNotBlank(),
			    () -> assertFalse(isAccessTokenExpired)
	    );
    }

	@Test
	void isAccessTokenExpired_recoverFromExpiredJwtException_returnTrue() {
		// given
		final String expiredAccessToken = jwtTokenService.generateAccessTokenCustomExpiration(tokenRequestOk, testDevice(), 0L);

		// when
        final boolean isAccessTokenExpired = jwtTokenValidator.isAccessTokenExpired(expiredAccessToken);

        // then
		Assertions.assertAll(
				() -> assertThat(expiredAccessToken).isNotNull(),
				() -> assertThat(expiredAccessToken).isNotEmpty(),
				() -> assertThat(expiredAccessToken).isNotBlank(),
                () -> assertTrue(isAccessTokenExpired)
		);
	}

    @Test
    void isAccessTokenValid() {
		// given
	    final String accessToken = jwtTokenService.generateAccessToken(tokenRequestOk, testDevice());

	    // when
	    final boolean isAccessTokenValid = jwtTokenValidator.isAccessTokenValid(accessToken, prepareTestJwtUser());

	    // then
	    Assertions.assertAll(
			    () -> assertThat(accessToken).isNotNull(),
			    () -> assertThat(accessToken).isNotEmpty(),
			    () -> assertThat(accessToken).isNotBlank(),
			    () -> assertTrue(isAccessTokenValid)
	    );
    }
}