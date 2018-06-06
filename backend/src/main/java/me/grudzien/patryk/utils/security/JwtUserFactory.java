package me.grudzien.patryk.utils.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.domain.entities.registration.Role;

public final class JwtUserFactory {

	// disabling default constructor
	private JwtUserFactory() {

	}

	public static JwtUser create(final CustomUser customUser) {
		return JwtUser.Builder()
		              .id(customUser.getId())
		              .firstname(customUser.getFirstName())
		              .lastname(customUser.getLastName())
		              .password(customUser.getPassword())
		              .email(customUser.getEmail())
		              .roles(mapRolesToAuthorities(customUser.getRoles()))
		              .enabled(customUser.isEnabled())
		              .lastPasswordResetDate(customUser.getLastPasswordResetDate())
		              .build();
	}

	private static Collection<? extends GrantedAuthority> mapRolesToAuthorities(final Collection<Role> roles) {
		return roles.stream()
		            .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
		            .collect(Collectors.toList());
	}
}
