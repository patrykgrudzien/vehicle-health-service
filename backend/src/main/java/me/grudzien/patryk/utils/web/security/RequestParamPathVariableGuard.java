package me.grudzien.patryk.utils.web.security;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.web.RequestsDecoder;

import static me.grudzien.patryk.utils.log.LogMarkers.SECURITY_MARKER;

@Log4j2
@Component
public class RequestParamPathVariableGuard {

	private final UserDetailsService userDetailsService;
	private final RequestsDecoder requestsDecoder;

	@Autowired
	public RequestParamPathVariableGuard(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
	                                     final RequestsDecoder requestsDecoder) {
		Preconditions.checkNotNull(userDetailsService, "userDetailsService cannot be null!");
		Preconditions.checkNotNull(requestsDecoder, "requestsDecoder cannot be null!");

		this.userDetailsService = userDetailsService;
		this.requestsDecoder = requestsDecoder;
	}

	public boolean isUserEmailAuthenticated(@NonNull final String userEmailAddress) {
		log.info(SECURITY_MARKER, "Checking if request's param or path's variable can be accessed...");
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(requestsDecoder.decodeStringParam(userEmailAddress));
		return authentication != null && jwtUser.getEmail().equals(((JwtUser) authentication.getPrincipal()).getEmail());
	}
}
