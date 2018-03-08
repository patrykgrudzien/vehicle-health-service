package me.grudzien.patryk.service.security;

import lombok.extern.log4j.Log4j;

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

import me.grudzien.patryk.domain.entities.CustomUser;
import me.grudzien.patryk.domain.entities.Role;
import me.grudzien.patryk.repository.CustomUserRepository;

@Log4j
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
		final CustomUser customUser = customUserRepository.findByEmail(email);
		if (customUser == null) {
			log.error("No user found for specified email: " + email);
			throw new UsernameNotFoundException("No user found for specified email: " + email);
		}
		log.info("User with " + email + " found");
		return new User(customUser.getEmail(), customUser.getPassword(), mapRolesToAuthorities(customUser.getRoles()));
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(final Collection<Role> roles) {
		return roles.stream()
		            .map(role -> new SimpleGrantedAuthority(role.getName()))
		            .collect(Collectors.toList());
	}
}
