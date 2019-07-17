package me.grudzien.patryk.oauth2.utils.rest;

import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.context.request.WebRequest;

import me.grudzien.patryk.oauth2.handler.CustomOAuth2AuthenticationFailureHandler;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.resource.RegistrationResource;

/**
 * I'm using that custom RestTemplate inside:
 * {@link me.grudzien.patryk.oauth2.service.google.impl.GooglePrincipalServiceImpl}
 * which calls:
 * {@link RegistrationResource#createUserAccount(UserRegistrationDto, WebRequest)} controller.
 * That controller calls than it's service which can throw some exception and to allow all that flow to proceed,
 * I override {@link DefaultResponseErrorHandler} and I don't handle any errors what allows OAuth2 flow to catch failures by:
 * {@link CustomOAuth2AuthenticationFailureHandler}.
 */
@Log4j2
public class CustomRestTemplateResponseErrorHandler extends DefaultResponseErrorHandler {

	@Override
	public void handleError(final ClientHttpResponse response) {
		log.error("handlerError() method omitted.");
	}

	@Override
	protected boolean hasError(final HttpStatus statusCode) {
		return statusCode.is4xxClientError();
	}
}
