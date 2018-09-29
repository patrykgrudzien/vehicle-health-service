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

import static me.grudzien.patryk.domain.enums.AppFLow.ACCOUNT_ALREADY_ENABLED;
import static me.grudzien.patryk.domain.enums.AppFLow.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.domain.enums.AppFLow.GOOGLE_REDIRECTION_SUCCESSFUL;
import static me.grudzien.patryk.domain.enums.AppFLow.REGISTER_OAUTH2_PRINCIPAL;
import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_CREATION;
import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.utils.log.LogMarkers.METHOD_INVOCATION_MARKER;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.domain.enums.AppFLow;
import me.grudzien.patryk.domain.enums.SpringAppProfiles;

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
			switch (appFLow) {
				case ACCOUNT_ALREADY_ENABLED:
					log.info(METHOD_INVOCATION_MARKER, ACCOUNT_ALREADY_ENABLED.getDetermineUrlLogInfoMessage());
					return propertiesKeeper.corsOrigins().FRONT_END_MODULE;
				case VERIFICATION_TOKEN_CREATION:
					/**
					 * Creating base app URL for:
					 * {@link me.grudzien.patryk.events.registration.RegistrationCompleteListener#createVerificationTokenAndSendEmail(
					 * me.grudzien.patryk.events.registration.OnRegistrationCompleteEvent)}
					 *
					 * @return base app URL which is used to redirect user to specific screen on UI.
					 */
					log.info(METHOD_INVOCATION_MARKER, VERIFICATION_TOKEN_CREATION.getDetermineUrlLogInfoMessage());
					return propertiesKeeper.corsOrigins().BACK_END_MODULE;
				case CONFIRM_REGISTRATION:
					log.info(METHOD_INVOCATION_MARKER, CONFIRM_REGISTRATION.getDetermineUrlLogInfoMessage());
					return propertiesKeeper.corsOrigins().FRONT_END_MODULE;
				case GOOGLE_REDIRECTION_SUCCESSFUL:
					log.info(METHOD_INVOCATION_MARKER, GOOGLE_REDIRECTION_SUCCESSFUL.getDetermineUrlLogInfoMessage());
					return propertiesKeeper.corsOrigins().FRONT_END_MODULE;
				case REGISTER_OAUTH2_PRINCIPAL:
					log.info(METHOD_INVOCATION_MARKER, REGISTER_OAUTH2_PRINCIPAL.getDetermineUrlLogInfoMessage());
					return propertiesKeeper.corsOrigins().BACK_END_MODULE;
				default:
					log.error(EXCEPTION_MARKER, "Unknown flow... Cannot determine app url where user will be redirected...");
					return null;
			}
		}
	}

	/**
	 * Method which creates base context path on Heroku environment.
	 *
	 * @return base context path (Heroku environment).
	 */
	private String getHerokuBaseUrl() {
		log.info(METHOD_INVOCATION_MARKER, "Created base context path for (HEROKU) env.");
		return propertiesKeeper.heroku().HEROKU_BASE_CONTEXT_PATH;
	}
}
