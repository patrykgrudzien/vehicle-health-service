package me.grudzien.patryk.utils.web;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import java.util.Arrays;

import me.grudzien.patryk.utils.app.AppFLow;
import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.utils.app.SpringAppProfiles;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.isIn;

import static me.grudzien.patryk.utils.app.AppFLow.ACCOUNT_ALREADY_ENABLED;
import static me.grudzien.patryk.utils.app.AppFLow.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.utils.app.AppFLow.REGISTER_OAUTH2_PRINCIPAL;
import static me.grudzien.patryk.utils.app.AppFLow.SYSTEM_COULD_NOT_ENABLE_USER_ACCOUNT;
import static me.grudzien.patryk.utils.app.AppFLow.USER_LOGGED_IN_USING_GOOGLE;
import static me.grudzien.patryk.utils.app.AppFLow.VERIFICATION_TOKEN_CREATION;

@Log4j2
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ContextPathsResolver implements InitializingBean {

	private static String ACTIVE_SPRING_PROFILE;

	private final Environment environment;
	private final PropertiesKeeper propertiesKeeper;

	@Autowired
	public ContextPathsResolver(final Environment environment, final PropertiesKeeper propertiesKeeper) {
		Preconditions.checkNotNull(environment, "environment cannot be null!");
		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
		this.environment = environment;
		this.propertiesKeeper = propertiesKeeper;
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
						return propertiesKeeper.corsOrigins().FRONT_END_MODULE;
					}),
					Case($(isIn(VERIFICATION_TOKEN_CREATION, REGISTER_OAUTH2_PRINCIPAL)), flow -> {
						log.info(flow.getDetermineUrlLogInfoMessage());
						return propertiesKeeper.corsOrigins().BACK_END_MODULE;
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
		return propertiesKeeper.heroku().HEROKU_BASE_CONTEXT_PATH;
	}
}
