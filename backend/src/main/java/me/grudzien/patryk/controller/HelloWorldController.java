package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j
@RestController
@RequestMapping("/api")
public class HelloWorldController {

	private static final String HELLO_TEXT = "Hello World from Full-Stack Web App";

	@RequestMapping("/hello")
	public @ResponseBody String helloWorld() {
		log.info("GET called on /hello resource");
		return HELLO_TEXT;
	}
}
