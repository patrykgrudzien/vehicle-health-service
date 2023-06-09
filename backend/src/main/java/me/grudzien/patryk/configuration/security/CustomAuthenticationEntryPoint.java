package me.grudzien.patryk.configuration.security;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.configuration.filters.ServletExceptionHandlerFilter;
import me.grudzien.patryk.jwt.exception.handler.JwtExceptionsHandler;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.HttpResponseCustomizer;
import me.grudzien.patryk.utils.web.model.AuthenticationEntryPointResponse;
import me.grudzien.patryk.utils.web.model.CustomResponse.SecurityStatus;

/**
 * IMPORTANT NOTE:
 * {@link JwtExceptionsHandler} only handles exceptions come from @Controller classes that's
 * why all exceptions which come from JWT will be omitted (they are specific to Servlet itself) !!!
 *
 * Exceptions come from JWT are handled by {@link ServletExceptionHandlerFilter}
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final LocaleMessagesCreator localeMessagesCreator;

	@Autowired
	public CustomAuthenticationEntryPoint(final LocaleMessagesCreator localeMessagesCreator) {
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		this.localeMessagesCreator = localeMessagesCreator;
	}

	/**
	 * This method is called as fallback when user wants to retrieve secured information but does not provide (access_token) at all.
	 * @param authException exception when user does not provide (access_token) at all.
	 */
	@Override
	public void commence(final HttpServletRequest request, final HttpServletResponse response,
	                     final AuthenticationException authException) {
		log.info(authException.getMessage());
		final String bodyMessage = localeMessagesCreator.buildLocaleMessage("secured-resource-message");
        HttpResponseCustomizer.customizeHttpResponse(response, HttpStatus.UNAUTHORIZED,
                                                     AuthenticationEntryPointResponse.buildBodyMessage(bodyMessage, SecurityStatus.UNAUTHENTICATED));
	}
}
