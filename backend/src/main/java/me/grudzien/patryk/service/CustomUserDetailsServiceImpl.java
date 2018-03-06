package me.grudzien.patryk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import me.grudzien.patryk.domain.dto.UserRegistrationDto;
import me.grudzien.patryk.domain.entities.CustomUser;
import me.grudzien.patryk.domain.entities.Role;
import me.grudzien.patryk.repository.UserRepository;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	@Autowired
	public CustomUserDetailsServiceImpl(final UserRepository userRepository, final BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
		final CustomUser customUser = this.findByEmail(email);
		if (customUser == null) {
			throw new UsernameNotFoundException("Invalid email or password");
		}
		return new User(customUser.getEmail(), customUser.getPassword(), mapRolesToAuthorities(customUser.getRoles()));
	}

	@Override
	public CustomUser findByEmail(final String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public CustomUser save(final UserRegistrationDto userRegistrationDto) {
		final CustomUser customUser = CustomUser.Builder()
		                                        .firstName(userRegistrationDto.getFirstName())
		                                        .lastName(userRegistrationDto.getLastName())
		                                        .email(userRegistrationDto.getEmail())
		                                        .password(passwordEncoder.encode(userRegistrationDto.getPassword()))
		                                        .roles(Collections.singleton(new Role("ROLE_USER")))
		                                        .build();
		return userRepository.save(customUser);
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(final Collection<Role> roles) {
		return roles.stream()
		            .map(role -> new SimpleGrantedAuthority(role.getName()))
		            .collect(Collectors.toList());
	}
}