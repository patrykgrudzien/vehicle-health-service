package me.grudzien.patryk.authentication.service.impl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import me.grudzien.patryk.authentication.model.factory.JwtUserFactory;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.repository.CustomUserRepository;

import static com.google.common.base.Preconditions.checkNotNull;

import static me.grudzien.patryk.authentication.service.impl.MyUserDetailsService.*;

@Slf4j
@Service
@Transactional
@CacheConfig(cacheNames = PRINCIPAL_USER_CACHE_NAME)
public class MyUserDetailsService implements UserDetailsService {

	public static final String BEAN_NAME = "myUserDetailsService";
	public static final String PRINCIPAL_USER_CACHE_NAME = "principal-user";

	private final CustomUserRepository customUserRepository;

	@Autowired
	public MyUserDetailsService(final CustomUserRepository customUserRepository) {
		checkNotNull(customUserRepository, "customUserRepository cannot be null!");
		this.customUserRepository = customUserRepository;
	}

	/**
	 * Method returning object that implements {@link UserDetails} interface, null otherwise.
	 *
	 * @param email used to retrieve {@link CustomUser} entity.
	 * @return {@link CustomUser} entity, null otherwise.
	 */
	@Override
	@Cacheable(key = "#email", condition = "#email != null && !#email.equals(\"\")", unless = "#result == null")
	public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
		log.info("(NO CACHE FOUND) => method execution...");
		return Optional.ofNullable(customUserRepository.findByEmail(email))
		               .map(foundUser -> {
		               	    log.info("User with {} address found.", email);
		               	    return JwtUserFactory.createFrom(foundUser);
		               })
		               .orElseGet(() -> {
		               	    log.error("No user found for specified email: {}", email);
		               	    return null;
		               });
	}
}
