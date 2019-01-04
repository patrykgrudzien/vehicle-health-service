package me.grudzien.patryk.util.web.security;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import java.util.Optional;

import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.service.login.impl.MyUserDetailsService;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.util.web.RequestsDecoder;

import static me.grudzien.patryk.util.log.LogMarkers.SECURITY_MARKER;

@Log4j2
@Component
public class RequestParamPathVariableGuard {

	private final UserDetailsService userDetailsService;
	private final RequestsDecoder requestsDecoder;
	private final LocaleMessagesCreator localeMessagesCreator;

	@Autowired
	public RequestParamPathVariableGuard(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
	                                     final RequestsDecoder requestsDecoder, final LocaleMessagesCreator localeMessagesCreator) {
		Preconditions.checkNotNull(userDetailsService, "userDetailsService cannot be null!");
		Preconditions.checkNotNull(requestsDecoder, "requestsDecoder cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");

		this.userDetailsService = userDetailsService;
		this.requestsDecoder = requestsDecoder;
		this.localeMessagesCreator = localeMessagesCreator;
	}

	public boolean isUserEmailAuthenticated(@NonNull final String userEmailAddress) {
		log.info(SECURITY_MARKER, "Checking if request's param or path's variable can be accessed...");
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final Optional<JwtUser> jwtUser = Optional.ofNullable((JwtUser) userDetailsService.loadUserByUsername(requestsDecoder.decodeStringParam(userEmailAddress)));
		return jwtUser.map(JwtUser::getEmail)
		              .map(email -> authentication != null && email.equals(((JwtUser) authentication.getPrincipal()).getEmail()))
		              .orElseThrow(() -> new UsernameNotFoundException(
		              		localeMessagesCreator.buildLocaleMessageWithParam("user-not-found-by-email", userEmailAddress)));
	}
}
