package me.grudzien.patryk.utils.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.domain.entities.registration.Role;

public final class JwtUserFactory {

	// disabling default constructor
	private JwtUserFactory() {}

	public static JwtUser create(final CustomUser customUser) {
		return new JwtUser(customUser.getId(), customUser.getUsername(), customUser.getFirstName(), customUser.getLastName(),
		                   customUser.getPassword(), customUser.getEmail(), mapRolesToAuthorities(customUser.getRoles()),
		                   customUser.isEnabled(), customUser.getLastPasswordResetDate());
	}

	private static Collection<? extends GrantedAuthority> mapRolesToAuthorities(final Collection<Role> roles) {
		return roles.stream()
		            .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
		            .collect(Collectors.toList());
	}
}
