package me.grudzien.patryk.integration.service.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;

import com.google.common.collect.Sets;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import me.grudzien.patryk.authentication.model.dto.JwtUser;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.model.entity.Role;
import me.grudzien.patryk.utils.app.ApplicationZone;
import me.grudzien.patryk.registration.model.enums.RegistrationProvider;
import me.grudzien.patryk.registration.model.enums.RoleName;
import me.grudzien.patryk.oauth2.utils.CacheManagerHelper;
import me.grudzien.patryk.registration.repository.CustomUserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static me.grudzien.patryk.TestsUtils.TEST_EMAIL;
import static me.grudzien.patryk.authentication.service.MyUserDetailsService.BEAN_NAME;
import static me.grudzien.patryk.authentication.service.MyUserDetailsService.PRINCIPAL_USER_CACHE_NAME;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class MyUserDetailsServiceIT {

	@Autowired
	@Qualifier(BEAN_NAME)
	private UserDetailsService userDetailsService;

	@Autowired
	private CacheManagerHelper cacheManagerHelper;

	@MockBean
	private CustomUserRepository customUserRepository;

	private Object cachedJwtUser;

	@AfterEach
	void tearDown() {
		cacheManagerHelper.clearAllCache(PRINCIPAL_USER_CACHE_NAME);
		assertThat(cacheManagerHelper.getCacheValue(PRINCIPAL_USER_CACHE_NAME, TEST_EMAIL, UserDetails.class)).isEqualTo(Optional.empty());
	}

	@Test
	@DisplayName("User loaded successfully from cache. Cache: (" + PRINCIPAL_USER_CACHE_NAME + ") populated. Second fetch doesn't invoke database.")
	void testLoadUserByUsernameSuccessful() {
		// given
		BDDMockito.given(customUserRepository.findByEmail(anyString())).willReturn(getTestCustomUser());

		// when
		userDetailsService.loadUserByUsername(TEST_EMAIL);
		userDetailsService.loadUserByUsername(TEST_EMAIL);

		// then
        cachedJwtUser = cacheManagerHelper.getCacheValue(PRINCIPAL_USER_CACHE_NAME, TEST_EMAIL, UserDetails.class).orElse(null);

		verify(customUserRepository, times(1)).findByEmail(TEST_EMAIL);
		Assertions.assertAll(
				() -> Assertions.assertNotNull(cachedJwtUser),
				() -> assertThat(cachedJwtUser).isInstanceOf(JwtUser.class)
		);
	}

	private static Stream<Arguments> cacheNotPopulatedTestData() {
		return Stream.of(
				Arguments.arguments((Object) null),
				Arguments.arguments(""),
				Arguments.arguments(TEST_EMAIL)
		);
	}

	@DisplayName("Cannot load user from cache. Cache: (" + PRINCIPAL_USER_CACHE_NAME + ") NOT populated!")
	@ParameterizedTest(name = "Given ({0}) will return (null). @Cacheable \"condition\", \"unless\" attributes don't match!")
	@MethodSource("cacheNotPopulatedTestData")
	void cacheNotPopulated(final String username) {
		// given
		BDDMockito.given(customUserRepository.findByEmail(null)).willReturn(null);
		BDDMockito.given(customUserRepository.findByEmail("")).willReturn(null);
		BDDMockito.given(customUserRepository.findByEmail(username)).willReturn(null);

		// when
		userDetailsService.loadUserByUsername(username);
		userDetailsService.loadUserByUsername(username);

		// then
		cachedJwtUser = cacheManagerHelper.getCacheValue(PRINCIPAL_USER_CACHE_NAME, TEST_EMAIL, UserDetails.class).orElse(null);

		verify(customUserRepository, times(2)).findByEmail(username);
		Assertions.assertAll(
				() -> Assertions.assertNull(cachedJwtUser)
		);
	}

	private CustomUser getTestCustomUser() {
		return CustomUser.Builder()
		                 .id(1L)
		                 .firstName("firstName")
		                 .lastName("lastName")
		                 .password("password")
		                 .profilePictureUrl("www.test-profile-photo.com")
		                 .registrationProvider(RegistrationProvider.CUSTOM)
		                 .email(TEST_EMAIL)
		                 .roles(Sets.newHashSet(new Role(RoleName.ROLE_USER)))
		                 .isEnabled(false)
		                 .lastPasswordResetDate(ZonedDateTime.now(ZoneId.of(ApplicationZone.POLAND.getZoneId())))
		                 .build();
	}
}