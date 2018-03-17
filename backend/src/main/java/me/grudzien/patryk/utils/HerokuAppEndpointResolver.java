package me.grudzien.patryk.utils;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;

@Log4j2
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class HerokuAppEndpointResolver implements InitializingBean {

	private static String ACTIVE_SPRING_PROFILE;

	private final Environment environment;
	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public HerokuAppEndpointResolver(final Environment environment, final CustomApplicationProperties customApplicationProperties) {
		this.environment = environment;
		log.info(LogMarkers.FLOW_MARKER, "{} bean injected.", Environment.class);

		this.customApplicationProperties = customApplicationProperties;
		log.info(LogMarkers.FLOW_MARKER, "{} bean injected.", CustomApplicationProperties.class);
	}

	@Override
	public void afterPropertiesSet() {
		log.info(LogMarkers.METHOD_INVOCATION_MARKER, "{} method called.", "afterPropertiesSet()");
		Arrays.stream(environment.getActiveProfiles())
		      .findFirst()
		      // taking active spring profile
		      .ifPresent(activeProfile -> ACTIVE_SPRING_PROFILE = activeProfile);
	}

	/**
	 * Method which creates base app URL for createVerificationTokenAndSendEmail() method in RegistrationCompleteListener
	 * based on active spring profile.
	 *
	 * @return base app URL which is used in e-mail sent to the user.
	 */
	public String determineBaseAppUrlForVerificationToken() {
		if (ACTIVE_SPRING_PROFILE.equals(SpringAppProfiles.HEROKU_DEPLOYMENT.getYmlName())) {
			return getHerokuAppBaseUrl();
		} else {
			log.info(LogMarkers.METHOD_INVOCATION_MARKER, "Created registration confirmation URL for LOCAL env.");
			return customApplicationProperties.getCorsOrigins().getBackEndModule();
		}
	}

	/**
	 * Method which creates base app URL for confirmRegistration() method in UserRegistrationController
	 * based on active spring profile.
	 *
	 * @return base app URL which is used to redirect user to confirmation screen on UI.
	 */
	public String determineBaseAppUrlForConfirmRegistration() {
		if (ACTIVE_SPRING_PROFILE.equals(SpringAppProfiles.HEROKU_DEPLOYMENT.getYmlName())) {
			return getHerokuAppBaseUrl();
		} else {
			log.info(LogMarkers.METHOD_INVOCATION_MARKER, "Created registration confirmation URL for LOCAL env.");
			return customApplicationProperties.getCorsOrigins().getFrontEndModule();
		}
	}

	/**
	 * Method which creates base app URL to Heroku environment.
	 *
	 * @return base app URL (Heroku environment).
	 */
	private String getHerokuAppBaseUrl() {
		log.info(LogMarkers.METHOD_INVOCATION_MARKER, "Created registration confirmation URL for HEROKU env.");
		return customApplicationProperties.getEndpoints().getHeroku().getAppUrl();
	}
}
