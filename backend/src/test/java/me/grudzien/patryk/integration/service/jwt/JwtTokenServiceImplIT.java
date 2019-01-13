package me.grudzien.patryk.integration.service.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.test.annotation.DirtiesContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import me.grudzien.patryk.TestsUtils;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.enums.security.JwtTokenClaims;
import me.grudzien.patryk.exception.security.NoEmailProvidedException;
import me.grudzien.patryk.service.jwt.JwtTokenClaimsRetriever;
import me.grudzien.patryk.service.jwt.JwtTokenService;
import me.grudzien.patryk.util.date.DateOperationsHelper;
import me.grudzien.patryk.util.i18n.LocaleMessagesHelper;

import static org.assertj.core.api.Assertions.assertThat;

import static me.grudzien.patryk.TestsUtils.DISABLE_ENCODING;
import static me.grudzien.patryk.TestsUtils.ENABLE_ENCODING;
import static me.grudzien.patryk.TestsUtils.NO_EXISTING_EMAIL;
import static me.grudzien.patryk.TestsUtils.TEST_EMAIL;
import static me.grudzien.patryk.TestsUtils.prepareAccessTokenRequest;
import static me.grudzien.patryk.TestsUtils.testDevice;
import static me.grudzien.patryk.util.jwt.JwtTokenConstants.AUDIENCE_WEB;
import static me.grudzien.patryk.util.jwt.JwtTokenConstants.TokenType;

@SpringBootTest
@DirtiesContext
class JwtTokenServiceImplIT {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private JwtTokenClaimsRetriever jwtTokenClaimsRetriever;

    @Autowired
    private LocaleMessagesHelper localeMessagesHelper;

	private JwtAuthenticationRequest tokenRequestOk;
	private JwtAuthenticationRequest tokenRequestNullEmail;
	private JwtAuthenticationRequest tokenRequestNoExistingEmail;

	private final DateOperationsHelper dateOperationsHelper = new DateOperationsHelper();

	@BeforeEach
	void setUp() {
		tokenRequestOk = prepareAccessTokenRequest(TEST_EMAIL, ENABLE_ENCODING);
		tokenRequestNullEmail = prepareAccessTokenRequest(null, DISABLE_ENCODING);
		tokenRequestNoExistingEmail = prepareAccessTokenRequest(NO_EXISTING_EMAIL, ENABLE_ENCODING);
	}

	@AfterEach
	void tearDown() {
		tokenRequestOk = null;
		tokenRequestNullEmail = null;
		tokenRequestNoExistingEmail = null;
	}

	@Test
    void generateAccessToken_successful() {
	    // when
	    final String accessToken = jwtTokenService.generateAccessToken(tokenRequestOk, testDevice());
	    final ZonedDateTime expiration = jwtTokenClaimsRetriever.getExpirationDateFromToken(accessToken)
	                                                            .orElseThrow(() -> new RuntimeException("No expiration claim found inside access token!"));
		final ZonedDateTime issuedAt = jwtTokenClaimsRetriever.getIssuedAtDateFromToken(accessToken)
		                                                      .orElseThrow(() -> new RuntimeException("No issuedAt claim found inside access token!"));
		// then
		Assertions.assertAll(
				() -> assertThat(accessToken).isNotNull(),
				() -> assertThat(accessToken).isNotEmpty(),
				() -> assertThat(accessToken).isNotBlank(),
				() -> assertThat(jwtTokenClaimsRetriever.getUserEmailFromToken(accessToken)).isEqualTo(Optional.of(TEST_EMAIL)),
				() -> assertThat(jwtTokenClaimsRetriever.getAudienceFromToken(accessToken)).isEqualTo(Optional.of(AUDIENCE_WEB)),
				() -> assertThat(dateOperationsHelper.getMinutesDifferenceBetween(expiration, issuedAt)).isEqualTo(15)
		);
    }

	@Test
    void generateAccessTokenCustomExpiration() {
        // when
        final String accessTokenWithCustomExpiration = jwtTokenService.generateAccessTokenCustomExpiration(tokenRequestOk, testDevice(), 30L);
		final ZonedDateTime expiration = jwtTokenClaimsRetriever.getExpirationDateFromToken(accessTokenWithCustomExpiration)
		                                                        .orElseThrow(() -> new RuntimeException("No expiration claim found inside access token!"));
		final ZonedDateTime issuedAt = jwtTokenClaimsRetriever.getIssuedAtDateFromToken(accessTokenWithCustomExpiration)
		                                                      .orElseThrow(() -> new RuntimeException("No issuedAt claim found inside access token!"));
		// then
		Assertions.assertAll(
				() -> assertThat(accessTokenWithCustomExpiration).isNotNull(),
				() -> assertThat(accessTokenWithCustomExpiration).isNotEmpty(),
				() -> assertThat(accessTokenWithCustomExpiration).isNotBlank(),
				() -> assertThat(jwtTokenClaimsRetriever.getUserEmailFromToken(accessTokenWithCustomExpiration)).isEqualTo(Optional.of(TEST_EMAIL)),
				() -> assertThat(jwtTokenClaimsRetriever.getAudienceFromToken(accessTokenWithCustomExpiration)).isEqualTo(Optional.of(AUDIENCE_WEB)),
				() -> assertThat(dateOperationsHelper.getMinutesDifferenceBetween(expiration, issuedAt)).isEqualTo(30)
		);
	}

    @Test
    void generateRefreshToken() {
        // when
        final String refreshToken = jwtTokenService.generateRefreshToken(tokenRequestOk, testDevice());
	    final Optional<ZonedDateTime> expiration = jwtTokenClaimsRetriever.getExpirationDateFromToken(refreshToken);

	    // then
	    Assertions.assertAll(
			    () -> assertThat(refreshToken).isNotNull(),
			    () -> assertThat(refreshToken).isNotEmpty(),
			    () -> assertThat(refreshToken).isNotBlank(),
			    () -> assertThat(jwtTokenClaimsRetriever.getAllClaimsFromToken(refreshToken).orElse(null)).hasSize(1),
			    () -> assertThat(jwtTokenClaimsRetriever.getUserEmailFromToken(refreshToken)).isEqualTo(Optional.of(TEST_EMAIL)),
			    () -> assertThat(expiration).isEqualTo(Optional.empty())
	    );
    }

    @Test
    void refreshAccessToken() {
        // given
	    final String refreshToken = jwtTokenService.generateRefreshToken(tokenRequestOk, testDevice());
	    final JwtAuthenticationRequest tokenRequestWithRefreshToken = prepareAccessTokenRequest(TEST_EMAIL, refreshToken, ENABLE_ENCODING);

        // when
        final String refreshedAccessToken = jwtTokenService.refreshAccessToken(tokenRequestWithRefreshToken, testDevice());

        // then
	    Assertions.assertAll(
			    () -> assertThat(refreshedAccessToken).isNotNull(),
			    () -> assertThat(refreshedAccessToken).isNotBlank(),
			    () -> assertThat(refreshedAccessToken).isNotEmpty(),
			    () -> assertThat(jwtTokenClaimsRetriever.getAllClaimsFromToken(refreshedAccessToken).orElse(null)).hasSize(5),
			    () -> assertThat(jwtTokenClaimsRetriever.getClaimFromToken(refreshedAccessToken, claims -> Optional.ofNullable(claims.get(JwtTokenClaims.USER_ROLES.getKey()))))
					          .isNotEqualTo(Optional.empty()),
			    () -> assertThat(jwtTokenClaimsRetriever.getClaimFromToken(refreshedAccessToken, claims -> Optional.ofNullable(claims.getSubject())))
					          .isNotEqualTo(Optional.empty()),
			    () -> assertThat(jwtTokenClaimsRetriever.getClaimFromToken(refreshedAccessToken, claims -> Optional.ofNullable(claims.getAudience())))
					          .isNotEqualTo(Optional.empty()),
			    () -> assertThat(jwtTokenClaimsRetriever.getClaimFromToken(refreshedAccessToken, claims -> Optional.ofNullable(claims.getIssuedAt())))
					          .isNotEqualTo(Optional.empty()),
			    () -> assertThat(jwtTokenClaimsRetriever.getClaimFromToken(refreshedAccessToken, claims -> Optional.ofNullable(claims.getExpiration())))
					          .isNotEqualTo(Optional.empty())
	    );
    }

    @Test
    void generateShortLivedToken() {
        // when
        final String shortLivedToken = jwtTokenService.generateShortLivedToken(TestsUtils.prepareCustomOAuth2OidcPrincipalUser());
	    final ZonedDateTime expiration = jwtTokenClaimsRetriever.getExpirationDateFromToken(shortLivedToken)
	                                                            .orElseThrow(() -> new RuntimeException("No expiration claim found inside short lived token!"));
	    final ZonedDateTime issuedAt = jwtTokenClaimsRetriever.getIssuedAtDateFromToken(shortLivedToken)
	                                                          .orElseThrow(() -> new RuntimeException("No expiration claim found inside short lived token!"));
	    // then
	    Assertions.assertAll(
			    () -> assertThat(shortLivedToken).isNotNull(),
			    () -> assertThat(shortLivedToken).isNotEmpty(),
			    () -> assertThat(shortLivedToken).isNotBlank(),
			    () -> assertThat(jwtTokenClaimsRetriever.getAllClaimsFromToken(shortLivedToken).orElse(null)).hasSize(4),
			    () -> assertThat(jwtTokenClaimsRetriever.getClaimFromToken(shortLivedToken, claims -> Optional.ofNullable(claims.getSubject())))
					          .isEqualTo(Optional.of(TEST_EMAIL)),
			    () -> assertThat(jwtTokenClaimsRetriever.getClaimFromToken(shortLivedToken, claims -> Optional.ofNullable(claims.get(StandardClaimNames.PICTURE))))
					          .isEqualTo(Optional.of("www.my-profile-photo.fakeUrl.com")),
			    () -> assertThat(dateOperationsHelper.getMinutesDifferenceBetween(expiration, issuedAt)).isEqualTo(2L)
	    );
    }

	private static Stream<Arguments> noEmailProvidedTestArguments() {
		return Stream.of(
				Arguments.arguments("en", new String[] {
						String.format("%s cannot be generated as NO email has been provided!", TokenType.ACCESS_TOKEN),
						String.format("%s cannot be generated as NO email has been provided!", TokenType.ACCESS_TOKEN),
						String.format("%s cannot be generated as NO email has been provided!", TokenType.REFRESH_TOKEN)
				}),
				Arguments.arguments("pl", new String[] {
						String.format("%s nie może zostać wygenerowany, dlatego że nie podano adresu e-mail!", TokenType.ACCESS_TOKEN),
						String.format("%s nie może zostać wygenerowany, dlatego że nie podano adresu e-mail!", TokenType.ACCESS_TOKEN),
						String.format("%s nie może zostać wygenerowany, dlatego że nie podano adresu e-mail!", TokenType.REFRESH_TOKEN)
				})
		);
	}

	@DisplayName("Generating tokens failed. No email provided!")
	@ParameterizedTest(name = "Exception messages with ({0}) locale language.")
	@MethodSource("noEmailProvidedTestArguments")
	void shouldFail_noEmailProvided(final String language, final String[] exceptionMessages) {
		// given
		localeMessagesHelper.setLocale(new Locale(language));

		// when
		final NoEmailProvidedException accessTokenException1 = Assertions.assertThrows(
				NoEmailProvidedException.class, () -> jwtTokenService.generateAccessToken(tokenRequestNullEmail, testDevice()));
		final NoEmailProvidedException accessTokenException2 = Assertions.assertThrows(
				NoEmailProvidedException.class, () -> jwtTokenService.generateAccessTokenCustomExpiration(tokenRequestNullEmail, testDevice(), 30L));
		final NoEmailProvidedException refreshTokenException = Assertions.assertThrows(
				NoEmailProvidedException.class, () -> jwtTokenService.generateRefreshToken(tokenRequestNullEmail, testDevice())
		);

		// then
		Assertions.assertAll(
				() -> assertThat(accessTokenException1.getMessage()).isEqualTo(exceptionMessages[0]),
				() -> assertThat(accessTokenException2.getMessage()).isEqualTo(exceptionMessages[1]),
				() -> assertThat(refreshTokenException.getMessage()).isEqualTo(exceptionMessages[2])
		);
	}

	private static Stream<Arguments> userNotFoundByEmailTestArguments() {
		return Stream.of(
				Arguments.arguments("en", new String[] {
						String.format("No user found for specified email: %s!", NO_EXISTING_EMAIL),
						String.format("No user found for specified email: %s!", NO_EXISTING_EMAIL),
						String.format("No user found for specified email: %s!", NO_EXISTING_EMAIL)
				}),
				Arguments.arguments("pl", new String[] {
						String.format("Nie znaleziono użytkownika o podanym adresie email: %s!", NO_EXISTING_EMAIL),
						String.format("Nie znaleziono użytkownika o podanym adresie email: %s!", NO_EXISTING_EMAIL),
						String.format("Nie znaleziono użytkownika o podanym adresie email: %s!", NO_EXISTING_EMAIL)
				})
		);
	}

	@DisplayName("Generating tokens failed. User not found by email!")
	@ParameterizedTest(name = "Exception messages with ({0}) locale language.")
	@MethodSource("userNotFoundByEmailTestArguments")
	void shouldFail_userNotFoundByEmail(final String language, final String[] exceptionMessages) {
		// given
		localeMessagesHelper.setLocale(new Locale(language));

		// when
		final UsernameNotFoundException accessTokenException1 = Assertions.assertThrows(
				UsernameNotFoundException.class, () -> jwtTokenService.generateAccessToken(tokenRequestNoExistingEmail, testDevice()));
		final UsernameNotFoundException accessTokenException2 = Assertions.assertThrows(
				UsernameNotFoundException.class, () -> jwtTokenService.generateAccessTokenCustomExpiration(tokenRequestNoExistingEmail, testDevice(), 30L));
		final UsernameNotFoundException refreshTokenException = Assertions.assertThrows(
				UsernameNotFoundException.class, () -> jwtTokenService.generateRefreshToken(tokenRequestNoExistingEmail, testDevice()));

		// then
		Assertions.assertAll(
				() -> assertThat(accessTokenException1.getMessage()).isEqualTo(exceptionMessages[0]),
				() -> assertThat(accessTokenException2.getMessage()).isEqualTo(exceptionMessages[1]),
				() -> assertThat(refreshTokenException.getMessage()).isEqualTo(exceptionMessages[2])
		);
	}
}