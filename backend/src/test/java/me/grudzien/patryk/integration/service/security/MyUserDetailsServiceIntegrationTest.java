package me.grudzien.patryk.integration.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import com.google.common.collect.Sets;

import java.util.Date;

import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.domain.entity.registration.CustomUser;
import me.grudzien.patryk.domain.entity.registration.Role;
import me.grudzien.patryk.domain.enums.registration.RegistrationProvider;
import me.grudzien.patryk.domain.enums.registration.RoleName;
import me.grudzien.patryk.oauth2.util.CacheHelper;
import me.grudzien.patryk.repository.registration.CustomUserRepository;
import me.grudzien.patryk.service.security.MyUserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Disabled("Disabled because of: net.sf.ehcache.CacheException: Another unnamed CacheManager already exists in the same VM.")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyUserDetailsServiceIntegrationTest {

	@Autowired
	@Qualifier(MyUserDetailsService.BEAN_NAME)
	private UserDetailsService userDetailsService;

	@Autowired
	private CacheHelper cacheHelper;

	@MockBean
	private CustomUserRepository customUserRepository;

	private Object cachedJwtUser;

	private static final String TEST_EMAIL = "test@email.com";

	@AfterEach
	void tearDown() {
		cacheHelper.clearCacheByName(MyUserDetailsService.PRINCIPAL_USER_CACHE_NAME);
		assertThat(cacheHelper.loadCache(MyUserDetailsService.PRINCIPAL_USER_CACHE_NAME, TEST_EMAIL, () -> "")).isEqualTo("");
	}

	@Test
	@DisplayName("User loaded successfully. Cache: (" + MyUserDetailsService.PRINCIPAL_USER_CACHE_NAME + ") populated. Second fetch doesn't invoke database.")
	void testLoadUserByUsernameSuccessful() {
		// given
		BDDMockito.given(customUserRepository.findByEmail(anyString())).willReturn(getTestCustomUser());

		// when
		userDetailsService.loadUserByUsername(TEST_EMAIL);
		userDetailsService.loadUserByUsername(TEST_EMAIL);

		// then
		cachedJwtUser = cacheHelper.loadCache(MyUserDetailsService.PRINCIPAL_USER_CACHE_NAME, TEST_EMAIL, () -> null);

		verify(customUserRepository, times(1)).findByEmail(TEST_EMAIL);
		Assertions.assertAll(
				() -> Assertions.assertNotNull(cachedJwtUser),
				() -> assertThat(cachedJwtUser).isInstanceOf(JwtUser.class)
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
		                 .lastPasswordResetDate(new Date())
		                 .build();
	}
}