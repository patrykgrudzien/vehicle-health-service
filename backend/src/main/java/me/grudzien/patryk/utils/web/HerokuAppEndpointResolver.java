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

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.enums.AppFLow;
import me.grudzien.patryk.domain.enums.SpringAppProfiles;

import static me.grudzien.patryk.domain.enums.AppFLow.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.domain.enums.AppFLow.ACCOUNT_ALREADY_ENABLED;
import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_CREATION;
import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.utils.log.LogMarkers.METHOD_INVOCATION_MARKER;

@Log4j2
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class HerokuAppEndpointResolver implements InitializingBean {

	private static String ACTIVE_SPRING_PROFILE;

	private final Environment environment;
	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public HerokuAppEndpointResolver(final Environment environment, final CustomApplicationProperties customApplicationProperties) {
		Preconditions.checkNotNull(environment, "environment cannot be null!");
		Preconditions.checkNotNull(customApplicationProperties, "customApplicationProperties cannot be null!");
		this.environment = environment;
		this.customApplicationProperties = customApplicationProperties;
	}

	@Override
	public void afterPropertiesSet() {
		log.info(METHOD_INVOCATION_MARKER, "{} method called.", "afterPropertiesSet()");
		Arrays.stream(environment.getActiveProfiles())
		      .findFirst()
		      // taking active spring profile
		      .ifPresent(activeProfile -> ACTIVE_SPRING_PROFILE = activeProfile);
	}

	public String determineBaseAppUrl(final AppFLow appFLow) {
		if (SpringAppProfiles.HEROKU_DEPLOYMENT.getYmlName().equals(ACTIVE_SPRING_PROFILE)) {
			return getHerokuAppBaseUrl();
		} else {
			switch (appFLow) {
				case ACCOUNT_ALREADY_ENABLED:
					log.info(METHOD_INVOCATION_MARKER, ACCOUNT_ALREADY_ENABLED.getDetermineUrlLogInfoMessage());
					return customApplicationProperties.getCorsOrigins().getFrontEndModule();
				case VERIFICATION_TOKEN_CREATION:
					/**
					 * Creating base app URL for:
					 * {@link me.grudzien.patryk.events.registration.RegistrationCompleteListener#createVerificationTokenAndSendEmail(me.grudzien.patryk.events.registration.OnRegistrationCompleteEvent)}
					 *
					 * @return base app URL which is used to redirect user to specific screen on UI.
					 */
					log.info(METHOD_INVOCATION_MARKER, VERIFICATION_TOKEN_CREATION.getDetermineUrlLogInfoMessage());
					return customApplicationProperties.getCorsOrigins().getBackEndModule();
				case CONFIRM_REGISTRATION:
					log.info(METHOD_INVOCATION_MARKER, CONFIRM_REGISTRATION.getDetermineUrlLogInfoMessage());
					return customApplicationProperties.getCorsOrigins().getFrontEndModule();
				default:
					log.error(EXCEPTION_MARKER, "Unknown flow... Cannot determine app url where user will be redirected...");
					return null;
			}
		}
	}

	/**
	 * Method which creates base app URL to Heroku environment.
	 *
	 * @return base app URL (Heroku environment).
	 */
	private String getHerokuAppBaseUrl() {
		log.info(METHOD_INVOCATION_MARKER, "Created base app URL for (HEROKU) env.");
		return customApplicationProperties.getEndpoints().getHeroku().getAppUrl();
	}
}
