package me.grudzien.patryk.authentication.model.factory;

import lombok.NoArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

import me.grudzien.patryk.authentication.model.dto.JwtUser;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.model.entity.Role;

import static lombok.AccessLevel.NONE;

@NoArgsConstructor(access = NONE)
public final class JwtUserFactory {

	public static JwtUser createFrom(final CustomUser customUser) {
		return JwtUser.Builder()
		              .id(customUser.getId())
		              .firstname(customUser.getFirstName())
		              .lastname(customUser.getLastName())
		              .password(customUser.getPassword())
                      .profilePictureUrl(customUser.getProfilePictureUrl())
                      .registrationProvider(customUser.getRegistrationProvider())
		              .email(customUser.getEmail())
		              .roles(mapRolesToAuthorities(customUser.getRoles()))
		              .enabled(customUser.isEnabled())
		              .lastPasswordResetDate(customUser.getLastPasswordResetDate())
		              .build();
	}

	public static Collection<? extends GrantedAuthority> mapRolesToAuthorities(final Collection<Role> roles) {
		return roles.stream()
		            .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
		            .collect(Collectors.toList());
	}
}
