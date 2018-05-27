package me.grudzien.patryk.service.security;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;

import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.repository.registration.CustomUserRepository;
import me.grudzien.patryk.utils.security.JwtUserFactory;

@Log4j2
@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {

	public static final String BEAN_NAME = "myUserDetailsService";
	private final CustomUserRepository customUserRepository;

	@Autowired
	public MyUserDetailsService(final CustomUserRepository customUserRepository) {
		this.customUserRepository = customUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
		log.info(FLOW_MARKER, "loadUserByUsername() inside >>>> MyUserDetailsService >>>> Spring Security");

		final CustomUser customUser = customUserRepository.findByEmail(email);
		if (customUser == null) {
			log.error(EXCEPTION_MARKER, "No user found for specified email: {}", email);
			throw new UsernameNotFoundException("No user found for specified email: " + email);
		} else {
			log.info(FLOW_MARKER, "User with {} address found.", email);
			return JwtUserFactory.create(customUser);
		}
	}
}
