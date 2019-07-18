package me.grudzien.patryk.utils.web;

import lombok.NoArgsConstructor;

import me.grudzien.patryk.oauth2.utils.OAuth2FlowDelegator;

import static lombok.AccessLevel.NONE;

@NoArgsConstructor(access = NONE)
public final class FrontendRoutesDefinitions {

    public static final String UI_ROOT_CONTEXT = "/ui**";
    public static final String REGISTRATION_FORM = "/registration-form";
    public static final String REGISTRATION_CONFIRMED = "/registration-confirmed";
    public static final String LOGIN = "/login";
    /**
     * Used for now only in: {@link OAuth2FlowDelegator#determineFlowBasedOnUrl(String)}
     */
    public static final String LOGOUT = "/logout";

    static final String REGISTRATION_CONFIRMED_USER_ALREADY_ENABLED = "/registration-confirmed?info=userAlreadyEnabled";
    static final String REGISTRATION_CONFIRMED_TOKEN_NOT_FOUND = "/registration-confirmed?error=tokenNotFound";
    static final String REGISTRATION_CONFIRMED_TOKEN_EXPIRED = "/registration-confirmed?error=tokenExpired";
    static final String REGISTRATION_CONFIRMED_SYSTEM_ERROR = "/registration-confirmed?error=systemEncounteredAnErrorOnEnablingAccount";
}
