package me.grudzien.patryk.oauth2.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import me.grudzien.patryk.domain.dto.login.JwtUser;

@Getter
@Setter
/**
 * It's done this way because I want
 * {@link me.grudzien.patryk.domain.dto.login.JwtUser}
 * to be set as required field during building
 * {@link me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser} object in:
 * {@link me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUserFactory#create(JwtUser)}.
 */
@Builder(builderMethodName = "hiddenBuilder")
@AllArgsConstructor
public class CustomOAuth2OidcPrincipalUser implements OidcUser, UserDetails, Serializable {

	private static final long serialVersionUID = 9136407623861455510L;

	@Getter(AccessLevel.NONE)
	private final JwtUser jwtUser;

	private Map<String, Object> attributes;
	private Map<String, Object> claims;
	private OidcUserInfo oidcUserInfo;
	private OidcIdToken oidcIdToken;

	public static CustomOAuth2OidcPrincipalUserBuilder Builder(final JwtUser jwtUser) {
		return hiddenBuilder().jwtUser(jwtUser);
	}

	@Override
	public String getName() {
		return jwtUser.getFirstname();
	}

	@Override
	public String getUsername() {
		return jwtUser.getEmail();
	}

	@Override
	public String getPassword() {
		return jwtUser.getPassword();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return jwtUser.getAuthorities();
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
		return true;
	}

	@Override
	public OidcUserInfo getUserInfo() {
		return oidcUserInfo;
	}

	@Override
	public OidcIdToken getIdToken() {
		return oidcIdToken;
	}
}
