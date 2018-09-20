package me.grudzien.patryk.service.security;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

import java.util.Optional;

import me.grudzien.patryk.repository.registration.CustomUserRepository;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
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
	private final LocaleMessagesCreator localeMessagesCreator;

	@Autowired
	public MyUserDetailsService(final CustomUserRepository customUserRepository, final LocaleMessagesCreator localeMessagesCreator) {
		Preconditions.checkNotNull(customUserRepository, "customUserRepository cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");

		this.customUserRepository = customUserRepository;
		this.localeMessagesCreator = localeMessagesCreator;
	}

	/**
	 * Method that returns {@link org.springframework.lang.NonNull} object, otherwise it throws an exception.
	 *
	 * @param email used to retrieve {@link me.grudzien.patryk.domain.entities.registration.CustomUser} entity.
	 * @return {@link me.grudzien.patryk.domain.entities.registration.CustomUser} entity.
	 * @throws UsernameNotFoundException if there is no user present in database.
	 */
	@Override
	@Cacheable(key = "#email", condition = "#email != null && !#email.equals(\"\")")
	public @NonNull UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
		log.info(METHOD_INVOCATION_MARKER, "(NO CACHE FOUND) => method execution...");
		return Optional.ofNullable(customUserRepository.findByEmail(email))
		               .map(foundUser -> {
		               	    log.info(FLOW_MARKER, "User with {} address found.", email);
		               	    return JwtUserFactory.create(foundUser);
		               })
		               .orElseThrow(() -> {
		               	    log.error(EXCEPTION_MARKER, "No user found for specified email: {}", email);
		               	    return new UsernameNotFoundException(localeMessagesCreator.buildLocaleMessageWithParam("user-not-found-by-email", email));
		               });
	}
}
