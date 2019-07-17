package me.grudzien.patryk.oauth2.utils.rest;

import org.springframework.web.client.RestTemplate;

public class CustomRestTemplateFactory {

	public static RestTemplate createRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new CustomRestTemplateResponseErrorHandler());
		return restTemplate;
	}
}
