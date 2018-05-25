package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j2;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import me.grudzien.patryk.utils.log.LogMarkers;

@Log4j2
@Controller
@Profile("heroku-deployment")
public class HerokuForwardingController {

	/**
	 * This controller only works when "heroku-deployment" spring profile is enabled.
	 * Endpoints listed in @RequestMapping come from "frontend" module "/src/router/allRoutes.js".
	 *
	 * It's required to forward requests to these paths to index.html because otherwise user will
	 * see 404 HTTP error (these endpoints are only Vue.js routes and there is no server/controller interaction).
	 *
	 * @return bundled index.html on page reloading, going back/forward browser action.
	 */
	@RequestMapping({
            "/server-health", "/about-me", "/registration-form", "/registration-confirmed", "/login", "/main-board"
	})
	public String forwardHerokuRequests() {
		log.info(LogMarkers.CONTROLLER_MARKER, "Request forwarded to index.html.");
		return "forward:/index.html";
	}
}