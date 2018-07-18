package me.grudzien.patryk.config.security;

import static org.springframework.http.HttpStatus.OK;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static me.grudzien.patryk.domain.dto.responses.AuthenticationEntryPointResponse.buildLocaleMessageWithExceptionCode;
import static me.grudzien.patryk.domain.dto.responses.CustomResponse.Codes.SECURED_RESOURCE_CODE;
import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;
import static me.grudzien.patryk.utils.web.CustomResponseCreator.customizeHttpResponse;

import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

/**
 * IMPORTANT NOTE:
 * {@link me.grudzien.patryk.handlers.exception.ExceptionHandlingController} only handles exceptions come from @Controller classes that's
 * why all exceptions which come from JWT will be omitted (they are specific to Servlet itself) !!!
 *
 * Exceptions come from JWT are handled by {@link me.grudzien.patryk.config.filters.ServletExceptionHandlerFilter}
 */
@Log4j2
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final LocaleMessagesCreator localeMessagesCreator;

	@Autowired
	public CustomAuthenticationEntryPoint(final LocaleMessagesCreator localeMessagesCreator) {
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		this.localeMessagesCreator = localeMessagesCreator;
	}

	@Override
	public void commence(final HttpServletRequest request, final HttpServletResponse response,
	                     final AuthenticationException authException) {
		log.info(FLOW_MARKER, authException.getMessage());
		customizeHttpResponse(response, OK, buildLocaleMessageWithExceptionCode(localeMessagesCreator.buildLocaleMessage("secured-resource-message"),
		                                                                        SECURED_RESOURCE_CODE));
	}
}