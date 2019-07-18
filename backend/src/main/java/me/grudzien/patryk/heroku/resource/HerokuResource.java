package me.grudzien.patryk.heroku.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import me.grudzien.patryk.utils.appplication.SpringAppProfiles;

import static me.grudzien.patryk.heroku.resource.HerokuResourceDefinition.UI_ROUTES;

/**
 * Resource responsible for forwarding UI specific requests to index.html.
 * Loaded to Spring Context on {@link SpringAppProfiles.YmlName#HEROKU_DEPLOYMENT} active profile.
 */
public interface HerokuResource {

    /**
     * Forwards all UI routes to index.html.
     * Required, because otherwise user will see 404 HTTP error.
     * These endpoints are only Vue.js routes and there is no server interaction.
     *
     * @return bundled index.html on page reloading, going back/forward browser action.
     */
    @RequestMapping(UI_ROUTES)
    String forwardRequestsToIndexHtml();
}