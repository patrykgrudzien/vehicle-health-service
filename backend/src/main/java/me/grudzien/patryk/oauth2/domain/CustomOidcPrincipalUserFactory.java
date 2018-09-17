package me.grudzien.patryk.oauth2.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.domain.entities.registration.Role;

public class CustomOidcPrincipalUserFactory {

	private CustomOidcPrincipalUserFactory() {
		throw new UnsupportedOperationException("Creating object of this class is not allowed!");
	}

	public static CustomOidcPrincipalUser create(final CustomUser customUser) {
		// TODO: FIX ME
		/*
		* CustomOidcPrincipalUser.Builder()
		              .id(customUser.getId())
		              .firstname(customUser.getFirstName())
		              .lastname(customUser.getLastName())
		              .password(customUser.getPassword())
		              .email(customUser.getEmail())
		              .roles(mapRolesToAuthorities(customUser.getRoles()))
		              .enabled(customUser.isEnabled())
		              .lastPasswordResetDate(customUser.getLastPasswordResetDate())
		              .build();
		* */
		return null;
	}

	private static Collection<? extends GrantedAuthority> mapRolesToAuthorities(final Collection<Role> roles) {
		return roles.stream()
		            .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
		            .collect(Collectors.toList());
	}
}
