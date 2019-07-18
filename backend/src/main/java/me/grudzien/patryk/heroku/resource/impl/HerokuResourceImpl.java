package me.grudzien.patryk.heroku.resource.impl;

import lombok.extern.log4j.Log4j2;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

import me.grudzien.patryk.heroku.resource.HerokuResource;

import static me.grudzien.patryk.utils.appplication.SpringAppProfiles.YmlName.HEROKU_DEPLOYMENT;

@Log4j2
@Controller
@Profile(HEROKU_DEPLOYMENT)
public class HerokuResourceImpl implements HerokuResource {

    @Override
    public String forwardRequestsToIndexHtml() {
        log.info("Request forwarded to index.html.");
        return "forward:/index.html";
    }
}
