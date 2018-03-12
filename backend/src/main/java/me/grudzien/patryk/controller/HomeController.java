package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;

@Log4j
@RestController
@RequestMapping("${custom.properties.endpoints.home.root}")
public class HomeController {

	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public HomeController(final CustomApplicationProperties customApplicationProperties) {
		this.customApplicationProperties = customApplicationProperties;
	}

	@GetMapping
	public @ResponseBody String home() {
		log.info("Inside: " + customApplicationProperties.getEndpoints().getHome().getRoot());
		return "Starting page...";
	}
}
