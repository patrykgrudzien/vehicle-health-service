package me.grudzien.patryk.oauth2.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@Builder(builderMethodName = "Builder")
@RequiredArgsConstructor
public class CustomOidcPrincipalUser implements OidcUser, UserDetails, CredentialsContainer {

	private static final long serialVersionUID = 9136407623861455510L;

	private String name;
	private String username;
	private OidcUserInfo oidcUserInfo;
	private OidcIdToken oidcIdToken;

	@Override
	public void eraseCredentials() {

	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return null;
	}

	@Override
	public Map<String, Object> getClaims() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public OidcUserInfo getUserInfo() {
		return oidcUserInfo;
	}

	@Override
	public OidcIdToken getIdToken() {
		return oidcIdToken;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
}
