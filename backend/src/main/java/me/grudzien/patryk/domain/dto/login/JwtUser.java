package me.grudzien.patryk.domain.dto.login;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collection;
import java.util.Date;

@Getter
@Builder(builderMethodName = "Builder")
@RequiredArgsConstructor
@JsonIgnoreProperties({"id", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "password", "lastPasswordResetDate",
                       "authorities", "username", "enabled", "roles"})
public class JwtUser implements UserDetails {

	private static final long serialVersionUID = -5701766234662554950L;

	private final Long id;
	private final String firstname;
	private final String lastname;
	private final String password;
	private final String email;
	private final Collection<? extends GrantedAuthority> roles;
	private final boolean enabled;
	private final Date lastPasswordResetDate;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
