package me.grudzien.patryk.oauth2.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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
@NoArgsConstructor
@AllArgsConstructor
public class CustomOAuth2OidcPrincipalUser implements OidcUser, UserDetails, Serializable {

	private static final long serialVersionUID = 2827505439577149874L;

	@Getter(AccessLevel.NONE)
	private JwtUser jwtUser;

	private AccountStatus accountStatus;

	private Map<String, Object> attributes;
	private OidcUserInfo oidcUserInfo;
	private OidcIdToken oidcIdToken;

	@Getter
	@AllArgsConstructor
    @JsonFormat(shape = Shape.OBJECT)
	public enum AccountStatus {
		LOGGED("2xx", "LOGGED", "User has been successfully logged in."),
		NOT_FOUND("4xx", "NOT_FOUND", "User cannot be found based on provided e-mail address!"),
		REGISTERED("2xx", "REGISTERED", "User has been successfully registered."),
		ALREADY_EXISTS("4xx", "ALREADY_EXISTS", "User cannot be registered because it's account already exists!"),
        USER_ACCOUNT_IS_LOCKED("4xx", "USER_ACCOUNT_IS_LOCKED", "User account is locked!"),
        USER_IS_DISABLED("4xx", "USER_IS_DISABLED", "User is disabled!"),
        USER_ACCOUNT_IS_EXPIRED("4xx", "USER_ACCOUNT_IS_EXPIRED", "User account is expired!"),
        CREDENTIALS_HAVE_EXPIRED("4xx", "CREDENTIALS_HAVE_EXPIRED", "Credentials have expired!"),
        REGISTRATION_PROVIDER_MISMATCH("4xx", "REGISTRATION_PROVIDER_MISMATCH", "Used different registration provider during account creation!"),
        BAD_CREDENTIALS("4xx", "BAD_CREDENTIALS", "Bad credentials!"),
        JWT_TOKEN_NOT_FOUND("4xx", "JWT_TOKEN_NOT_FOUND", "JWT Token not found!"),
        OAUTH2_FLOW_ERROR("4xx", "OAUTH2_FLOW_ERROR", "Unknown error occurred during OAuth2 flow using Google!"),
        NO_STATUS_PROVIDED_IN_EXCEPTION_HANDLER("4xx", "NO_STATUS_PROVIDED_IN_EXCEPTION_HANDLER", "There was no exception handler which could set specific account status!");

		private final String statusCode;
		private final String accountStatus;
		private final String accountStatusDescription;

        public static AccountStatus[] get2xxStatuses() {
            return Stream.of(AccountStatus.values())
                         .filter(status -> status.getStatusCode().startsWith("2"))
                         .toArray(AccountStatus[]::new);
        }

        public static AccountStatus[] get4xxStatuses() {
            return Stream.of(AccountStatus.values())
                         .filter(status -> status.getStatusCode().startsWith("4"))
                         .toArray(AccountStatus[]::new);
        }
	}

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
		return ObjectUtils.isEmpty(Optional.ofNullable(jwtUser)
		                                   .map(JwtUser::getAuthorities)
		                                   .isPresent()) ? jwtUser.getAuthorities() : AuthorityUtils.NO_AUTHORITIES;
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

	@Override
	public Map<String, Object> getClaims() {
		return oidcIdToken.getClaims();
	}
}
