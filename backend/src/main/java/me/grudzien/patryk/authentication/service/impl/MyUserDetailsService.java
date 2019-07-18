package me.grudzien.patryk.authentication.service.impl;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

import java.util.Optional;

import me.grudzien.patryk.authentication.model.factory.JwtUserFactory;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.repository.CustomUserRepository;

@Log4j2
@Service
@Transactional
@CacheConfig(cacheNames = MyUserDetailsService.PRINCIPAL_USER_CACHE_NAME)
public class MyUserDetailsService implements UserDetailsService {

	public static final String BEAN_NAME = "myUserDetailsService";
	public static final String PRINCIPAL_USER_CACHE_NAME = "principal-user";

	private final CustomUserRepository customUserRepository;

	@Autowired
	public MyUserDetailsService(final CustomUserRepository customUserRepository) {
		Preconditions.checkNotNull(customUserRepository, "customUserRepository cannot be null!");
		this.customUserRepository = customUserRepository;
	}

	/**
	 * Method returning object that implements {@link org.springframework.security.core.userdetails.UserDetails} interface, null otherwise.
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
		               	    return JwtUserFactory.create(foundUser);
		               })
		               .orElseGet(() -> {
		               	    log.error("No user found for specified email: {}", email);
		               	    return null;
		               });
	}
}
