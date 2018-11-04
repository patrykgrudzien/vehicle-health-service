package me.grudzien.patryk.oauth2.authentication.checkers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface AdditionalChecks<User extends UserDetails> {

	void additionalAuthenticationChecks(User user, Authentication authentication, String jwtSubjectIdentifier);
}
