package me.grudzien.patryk.oauth2.utils.rest;

import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import static me.grudzien.patryk.utils.log.LogMarkers.OAUTH2_MARKER;

/**
 * I'm using that custom RestTemplate inside:
 * {@link me.grudzien.patryk.oauth2.service.google.impl.GooglePrincipalServiceImpl}
 * which calls:
 * {@link me.grudzien.patryk.controller.registration.UserRegistrationController#registerUserAccount(
 * me.grudzien.patryk.domain.dto.registration.UserRegistrationDto, org.springframework.validation.BindingResult, org.springframework.web.context.request.WebRequest)}
 * controller.
 * That controller calls than it's service which can throw some exception and to allow all that flow to proceed,
 * I override {@link org.springframework.web.client.DefaultResponseErrorHandler} and I don't handle any errors what allows OAuth2 flow to catch failures by:
 * {@link me.grudzien.patryk.oauth2.handlers.CustomOAuth2AuthenticationFailureHandler}.
 */
@Log4j2
public class CustomRestTemplateResponseErrorHandler extends DefaultResponseErrorHandler {

	@Override
	public void handleError(final ClientHttpResponse response) {
		log.error(OAUTH2_MARKER, "handlerError() method omitted.");
	}

	@Override
	protected boolean hasError(final HttpStatus statusCode) {
		return statusCode.is4xxClientError();
	}
}
