package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j2;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import me.grudzien.patryk.PropertiesKeeper.FrontendRoutes;
import me.grudzien.patryk.domain.enums.SpringAppProfiles;
import me.grudzien.patryk.util.log.LogMarkers;

@Log4j2
@Controller
@Profile(SpringAppProfiles.YmlName.HEROKU_DEPLOYMENT)
public class HerokuForwardingController {

	/**
	 * This controller only works when "heroku-deployment" spring profile is enabled.
	 *
	 * It's required to forward requests to these UI paths to index.html because otherwise user will
	 * see 404 HTTP error (these endpoints are only Vue.js routes and there is no server/controller interaction).
	 *
	 * @return bundled index.html on page reloading, going back/forward browser action.
	 */
	@RequestMapping(FrontendRoutes.UI_CONTEXT_PATH + "**")
	public String forwardHerokuRequests() {
		log.info(LogMarkers.CONTROLLER_MARKER, "Request forwarded to index.html.");
		return "forward:/index.html";
	}
}