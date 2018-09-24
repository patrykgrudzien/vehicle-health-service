package me.grudzien.patryk.service.security;

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

import me.grudzien.patryk.repository.registration.CustomUserRepository;
import me.grudzien.patryk.utils.jwt.JwtUserFactory;

import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;
import static me.grudzien.patryk.utils.log.LogMarkers.METHOD_INVOCATION_MARKER;

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
	 * @param email used to retrieve {@link me.grudzien.patryk.domain.entities.registration.CustomUser} entity.
	 * @return {@link me.grudzien.patryk.domain.entities.registration.CustomUser} entity, null otherwise.
	 */
	@Override
	@Cacheable(key = "#email", condition = "#email != null && !#email.equals(\"\")")
	public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
		log.info(METHOD_INVOCATION_MARKER, "(NO CACHE FOUND) => method execution...");
		return Optional.ofNullable(customUserRepository.findByEmail(email))
		               .map(foundUser -> {
		               	    log.info(FLOW_MARKER, "User with {} address found.", email);
		               	    return JwtUserFactory.create(foundUser);
		               })
		               .orElseGet(() -> {
		               	    log.error(EXCEPTION_MARKER, "No user found for specified email: {}", email);
		               	    return null;
		               });
	}
}
