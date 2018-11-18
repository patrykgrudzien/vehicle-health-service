package me.grudzien.patryk.oauth2.controller;

import lombok.extern.log4j.Log4j2;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("${custom.properties.endpoints.api-context-path}")
public class FacebookOAuth2Controller {}
