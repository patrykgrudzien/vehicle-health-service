package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j2;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import me.grudzien.patryk.domain.enums.SpringAppProfiles;
import me.grudzien.patryk.utils.log.LogMarkers;

import static me.grudzien.patryk.Constants.FrontendRoutes;

@Log4j2
@Controller
@Profile(SpringAppProfiles.YmlName.HEROKU_DEPLOYMENT)
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
			FrontendRoutes.ABOUT_ME, FrontendRoutes.REGISTRATION_FORM, FrontendRoutes.REGISTRATION_CONFIRMED,
			FrontendRoutes.REGISTRATION_CONFIRMED_WILDCARD, FrontendRoutes.LOGIN, FrontendRoutes.MAIN_BOARD,
			FrontendRoutes.MAIN_BOARD_WILDCARD, FrontendRoutes.AUTHENTICATION_REQUIRED
	})
	public String forwardHerokuRequests() {
		log.info(LogMarkers.CONTROLLER_MARKER, "Request forwarded to index.html.");
		return "forward:/index.html";
	}
}