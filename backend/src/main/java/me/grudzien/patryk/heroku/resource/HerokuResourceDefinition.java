package me.grudzien.patryk.heroku.resource;

import lombok.NoArgsConstructor;

import me.grudzien.patryk.PropertiesKeeper.FrontendRoutes;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
final class HerokuResourceDefinition {

    static final String UI_ROUTES = FrontendRoutes.UI_CONTEXT_PATH + "**";
}
