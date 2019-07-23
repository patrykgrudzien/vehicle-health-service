package me.grudzien.patryk.oauth2.authentication.checkers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Additional authentication checks.
 *
 * @param <User> of type either:
 * {@link me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser}
 * or:
 * {@link me.grudzien.patryk.authentication.model.dto.JwtUser}
 */
public interface AdditionalChecks<User extends UserDetails> {

    /**
     * Checks if JWT was provided and if encrypted password (jwtSubjectIdentifier) matches
     * the encrypted password stored in the database.
     *
     * @param user {@link me.grudzien.patryk.authentication.model.dto.JwtUser}
     * @param authentication {@link me.grudzien.patryk.oauth2.authentication.model.CustomAuthenticationToken}
     * @param jwtSubjectIdentifier attribute provided by Google (used as a password which was used during registration)
     */
	void additionalAuthenticationChecks(User user, Authentication authentication, String jwtSubjectIdentifier);
}
