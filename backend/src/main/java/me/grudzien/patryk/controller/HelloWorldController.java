package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j;
import me.grudzien.patryk.constants.CorsOrigins;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Log4j
public class HelloWorldController {

	private static final String HELLO_TEXT = "Hello World from Full-Stack Web App";

	@CrossOrigin(origins = CorsOrigins.FRONTEND_MODULE)
	@RequestMapping("/hello")
	public @ResponseBody String helloWorld() {
		log.info("GET called on /hello resource");
		return HELLO_TEXT;
	}
}
