package me.grudzien.patryk.oauth2.authentication.checkers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface AdditionalChecks {

	void additionalAuthenticationChecks(UserDetails userDetails, Authentication authentication, String jwtSubjectIdentifier);
}
