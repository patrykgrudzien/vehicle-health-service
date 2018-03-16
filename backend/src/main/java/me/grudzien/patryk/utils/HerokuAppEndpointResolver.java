package me.grudzien.patryk.utils;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;

@Log4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class HerokuAppEndpointResolver implements InitializingBean {

	private static String ACTIVE_SPRING_PROFILE;
	private static final String HEROKU_PROFILE_NAME = "heroku-deployment";

	@Autowired
	private Environment environment;

	@Autowired
	private CustomApplicationProperties customApplicationProperties;

	@Override
	public void afterPropertiesSet() {
		Arrays.stream(environment.getActiveProfiles())
		      .findFirst()
		      .ifPresent(activeProfile -> ACTIVE_SPRING_PROFILE = activeProfile);
	}

	public String resolveBaseAppUrl() {
		if (ACTIVE_SPRING_PROFILE.equals(HEROKU_PROFILE_NAME)) {
			log.info("Created registration confirmation URL for HEROKU env.");
			return customApplicationProperties.getEndpoints().getHeroku().getAppUrl();
		} else {
			log.info("Created registration confirmation URL for LOCAL env.");
			return customApplicationProperties.getCorsOrigins().getBackEndModule();
		}
	}
}
