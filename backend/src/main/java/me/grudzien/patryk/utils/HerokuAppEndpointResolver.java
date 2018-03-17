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
	private static final String HEROKU_PROFILE_NAME = "heroku-deployment";

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
		      .ifPresent(activeProfile -> ACTIVE_SPRING_PROFILE = activeProfile);
	}

	/**
	 * Method which creates base app URL based on active spring profile.
	 * @return base app URL.
	 */
	public String determineBaseAppUrl() {
		if (ACTIVE_SPRING_PROFILE.equals(HEROKU_PROFILE_NAME)) {
			log.info(LogMarkers.METHOD_INVOCATION_MARKER, "Created registration confirmation URL for HEROKU env.");
			return customApplicationProperties.getEndpoints().getHeroku().getAppUrl();
		} else {
			log.info(LogMarkers.METHOD_INVOCATION_MARKER, "Created registration confirmation URL for LOCAL env.");
			return customApplicationProperties.getCorsOrigins().getBackEndModule();
		}
	}
}
