package me.grudzien.patryk.oauth2.authentication.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public final class CustomAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 6192413451740113683L;

	private UserDetails principal;
	private final String jwtToken;

	public CustomAuthenticationToken(final String jwtToken) {
		super(AuthorityUtils.NO_AUTHORITIES);
		this.jwtToken = jwtToken;
		setAuthenticated(Boolean.TRUE);
	}

    public CustomAuthenticationToken(final UserDetails principal, final String jwtToken,
                                     final Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.jwtToken = jwtToken;
		setAuthenticated(Boolean.TRUE);
	}

	@Override
	public Object getCredentials() {
		return jwtToken;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}
}
