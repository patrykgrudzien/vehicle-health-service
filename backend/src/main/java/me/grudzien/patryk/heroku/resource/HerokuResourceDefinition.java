package me.grudzien.patryk.heroku.resource;

import lombok.NoArgsConstructor;

import me.grudzien.patryk.utils.web.FrontendRoutesDefinitions;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class HerokuResourceDefinition {

    static final String UI_ROUTES = FrontendRoutesDefinitions.UI_ROOT_CONTEXT;

    public static final String HEROKU_APP_CONTEXT_PATH = "https://vehicle-health-service.herokuapp.com/ui";
}
