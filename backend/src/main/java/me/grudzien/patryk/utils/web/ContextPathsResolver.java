package me.grudzien.patryk.utils.web;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import me.grudzien.patryk.configuration.properties.cors.CustomCorsProperties;
import me.grudzien.patryk.heroku.resource.HerokuResourceDefinition;
import me.grudzien.patryk.utils.appplication.AppFLow;
import me.grudzien.patryk.utils.appplication.SpringAppProfiles;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.isIn;

import static me.grudzien.patryk.utils.appplication.AppFLow.ACCOUNT_ALREADY_ENABLED;
import static me.grudzien.patryk.utils.appplication.AppFLow.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.utils.appplication.AppFLow.REGISTER_OAUTH2_PRINCIPAL;
import static me.grudzien.patryk.utils.appplication.AppFLow.SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT;
import static me.grudzien.patryk.utils.appplication.AppFLow.USER_LOGGED_IN_USING_GOOGLE;
import static me.grudzien.patryk.utils.appplication.AppFLow.VERIFICATION_TOKEN_CREATION;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ContextPathsResolver implements InitializingBean {

	private static String ACTIVE_SPRING_PROFILE;

	private final Environment environment;
	private final CustomCorsProperties corsProperties;

	@Autowired
	public ContextPathsResolver(final Environment environment, final CustomCorsProperties corsProperties) {
        checkNotNull(environment, "environment cannot be null!");
        checkNotNull(corsProperties, "corsProperties cannot be null!");

		this.environment = environment;
        this.corsProperties = corsProperties;
	}

	@Override
	public void afterPropertiesSet() {
		Arrays.stream(environment.getActiveProfiles())
		      .findFirst()
		      // taking active spring profile
		      .ifPresent(activeProfile -> ACTIVE_SPRING_PROFILE = activeProfile);
	}

	public String determineUrlFor(final AppFLow appFLow) {
		if (SpringAppProfiles.HEROKU_DEPLOYMENT.getYmlName().equals(ACTIVE_SPRING_PROFILE)) {
			return getHerokuBaseUrl();
		} else {
			return Match(appFLow).of(
					Case($(isIn(ACCOUNT_ALREADY_ENABLED, CONFIRM_REGISTRATION, USER_LOGGED_IN_USING_GOOGLE, SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT)), flow -> {
						log.info(flow.getDetermineUrlLogInfoMessage());
						return corsProperties.getFrontEndAppLocalHostUrl();
					}),
					Case($(isIn(VERIFICATION_TOKEN_CREATION, REGISTER_OAUTH2_PRINCIPAL)), flow -> {
						log.info(flow.getDetermineUrlLogInfoMessage());
						return corsProperties.getBackEndAppLocalHostUrl();
					}),
					Case($(), () -> {
						log.error("Unknown flow... Cannot determine app url where user will be redirected...");
						return null;
					})
			);
		}
	}

	/**
	 * Method which creates base context path on Heroku environment.
	 *
	 * @return base context path (Heroku environment).
	 */
	private String getHerokuBaseUrl() {
		log.info("Created base context path for (HEROKU) env.");
		return HerokuResourceDefinition.HEROKU_APP_CONTEXT_PATH;
	}
}
