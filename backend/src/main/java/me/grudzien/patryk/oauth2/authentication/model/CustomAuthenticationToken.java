package me.grudzien.patryk.oauth2.authentication.model;

import lombok.Builder;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static java.lang.Boolean.TRUE;

public final class CustomAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 6192413451740113683L;

	private UserDetails principal;
	private final String jwtToken;

	public CustomAuthenticationToken(final String jwtToken) {
		super(AuthorityUtils.NO_AUTHORITIES);
		this.jwtToken = jwtToken;
		setAuthenticated(TRUE);
	}

    @Builder(builderMethodName = "Builder")
    public CustomAuthenticationToken(final UserDetails principal, final String jwtToken,
                                     final Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.jwtToken = jwtToken;
		setAuthenticated(TRUE);
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
