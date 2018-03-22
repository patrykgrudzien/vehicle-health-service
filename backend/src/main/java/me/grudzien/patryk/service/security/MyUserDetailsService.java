package me.grudzien.patryk.service.security;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.domain.entities.registration.Role;
import me.grudzien.patryk.repository.registration.CustomUserRepository;
import me.grudzien.patryk.utils.log.LogMarkers;

@Log4j2
@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {

	private final CustomUserRepository customUserRepository;

	@Autowired
	public MyUserDetailsService(final CustomUserRepository customUserRepository) {
		this.customUserRepository = customUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
		log.info(LogMarkers.FLOW_MARKER, "loadUserByUsername() inside >>>> MyUserDetailsService >>>> Spring Security");

		final CustomUser customUser = customUserRepository.findByEmail(email);
		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;
		if (customUser == null) {
			log.error("No user found for specified email: " + email);
			throw new UsernameNotFoundException("No user found for specified email: " + email);
		}
		log.info("User with " + email + " found");
		return new User(customUser.getEmail(), customUser.getPassword(), customUser.isEnabled(), accountNonExpired, credentialsNonExpired,
		                accountNonLocked, mapRolesToAuthorities(customUser.getRoles()));
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(final Collection<Role> roles) {
		return roles.stream()
		            .map(role -> new SimpleGrantedAuthority(role.getName()))
		            .collect(Collectors.toList());
	}
}
