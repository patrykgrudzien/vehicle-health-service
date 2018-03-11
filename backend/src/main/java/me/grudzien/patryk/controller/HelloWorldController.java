package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;

@Log4j
@RestController
@RequestMapping("${custom.properties.endpoints.api.home}")
public class HelloWorldController {

	private static final String HELLO_TEXT = "Server works! Hello there!";

	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public HelloWorldController(final CustomApplicationProperties customApplicationProperties) {
		this.customApplicationProperties = customApplicationProperties;
	}

	@RequestMapping("${custom.properties.endpoints.api.hello}")
	public @ResponseBody String helloWorld() {
		log.info("Inside: " + customApplicationProperties.getEndpoints().getApi().getHomeHello());
		return HELLO_TEXT;
	}
}
