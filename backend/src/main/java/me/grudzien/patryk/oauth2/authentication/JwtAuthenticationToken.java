package me.grudzien.patryk.oauth2.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -8493716580205865947L;

	private UserDetails principal;
	private final String jwtToken;

	public JwtAuthenticationToken(final String jwtToken) {
		super(AuthorityUtils.NO_AUTHORITIES);
		this.jwtToken = jwtToken;
	}

	public JwtAuthenticationToken(final UserDetails principal, final String jwtToken, final Collection<? extends GrantedAuthority> authorities) {
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
