//package me.grudzien.patryk.oauth2.domain;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//
//import org.springframework.security.core.CredentialsContainer;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.oauth2.core.oidc.OidcIdToken;
//import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import java.util.Collection;
//import java.util.Map;
//
//@Getter
//@Builder(builderMethodName = "Builder")
//@AllArgsConstructor
//public class CustomOidcPrincipalUser implements OAuth2User, OidcUser, UserDetails, CredentialsContainer {
//
// TODO
//
//	private static final long serialVersionUID = 9136407623861455510L;
//
//	private String name;
//	private String username;
//	private String password;
//	private Map<String, Object> claims;
//	private Map<String, Object> attributes;
//	private Collection<? extends GrantedAuthority> roles;
//	private OidcUserInfo oidcUserInfo;
//	private OidcIdToken oidcIdToken;
//	private boolean enabled;
//
//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		return roles;
//	}
//
//	@Override
//	public Map<String, Object> getAttributes() {
//		return attributes;
//	}
//
//	@Override
//	public Map<String, Object> getClaims() {
//		return claims;
//	}
//
//	@Override
//	public String getName() {
//		return name;
//	}
//
//	@Override
//	public OidcUserInfo getUserInfo() {
//		return oidcUserInfo;
//	}
//
//	@Override
//	public OidcIdToken getIdToken() {
//		return oidcIdToken;
//	}
//
//	@Override
//	public String getPassword() {
//		return password;
//	}
//
//	@Override
//	public String getUsername() {
//		return username;
//	}
//
//	@Override
//	public boolean isAccountNonExpired() {
//		return false;
//	}
//
//	@Override
//	public boolean isAccountNonLocked() {
//		return false;
//	}
//
//	@Override
//	public boolean isCredentialsNonExpired() {
//		return false;
//	}
//
//	@Override
//	public boolean isEnabled() {
//		return enabled;
//	}
//
//	@Override
//	public void eraseCredentials() {
//		name = null;
//		username = null;
//		password = null;
//		claims = null;
//		attributes = null;
//		roles = null;
//		oidcUserInfo = null;
//		oidcIdToken = null;
//	}
//}
