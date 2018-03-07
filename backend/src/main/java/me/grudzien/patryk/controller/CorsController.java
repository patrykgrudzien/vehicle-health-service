package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.CrossOrigin;

import me.grudzien.patryk.constants.CorsOrigins;

@Log4j
@CrossOrigin(origins = CorsOrigins.FRONTEND_MODULE)
@Profile({"dev-home", "dev-office"})
public class CorsController {}
