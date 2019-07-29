package me.grudzien.patryk.authentication.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collection;

import me.grudzien.patryk.registration.model.enums.RegistrationProvider;

import static lombok.AccessLevel.NONE;

@Getter
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@JsonIgnoreProperties({
        "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "password",
        "lastPasswordResetDate", "authorities", "username", "enabled", "roles"
})
public class JwtUser implements UserDetails, Serializable {

	private static final long serialVersionUID = 8787822344111596162L;

	private final Long id;
	private final String firstname;
	private final String lastname;
	private final String password;
	private final String email;
	private final ZonedDateTime lastPasswordResetDate;

	@Setter
	private String profilePictureUrl;

	@Setter
    private RegistrationProvider registrationProvider;

	// disabling getter for "roles" to stay consistent and force usage of "getAuthorities()" instead
	@Getter(NONE)
	private final Collection<? extends GrantedAuthority> roles;

	// disabling getter for "enabled" to stay consistent and force usage of overriden "isEnabled()" instead
	@Getter(NONE)
	private final boolean enabled;

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
