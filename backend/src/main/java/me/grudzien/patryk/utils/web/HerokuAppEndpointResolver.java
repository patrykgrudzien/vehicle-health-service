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
import me.grudzien.patryk.domain.enums.BaseAppActions;
import me.grudzien.patryk.domain.enums.SpringAppProfiles;

import static me.grudzien.patryk.domain.enums.BaseAppActions.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.domain.enums.BaseAppActions.USER_ALREADY_ENABLED;
import static me.grudzien.patryk.domain.enums.BaseAppActions.VERIFICATION_TOKEN_CREATION;
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

	public String determineBaseAppUrlForAction(final BaseAppActions baseAppActions) {
		if (SpringAppProfiles.HEROKU_DEPLOYMENT.getYmlName().equals(ACTIVE_SPRING_PROFILE)) {
			return getHerokuAppBaseUrl();
		} else {
			switch (baseAppActions) {
				case USER_ALREADY_ENABLED:
					/**
					 * Creating base app URL for:
					 * {@link me.grudzien.patryk.service.registration.UserRegistrationServiceImpl#confirmRegistration(String, javax.servlet.http.HttpServletResponse)}
					 *
					 * @return Base app URL which is used to redirect user to (user already enabled) screen on UI.
					 */
					log.info(METHOD_INVOCATION_MARKER, USER_ALREADY_ENABLED.getLogInfoMessage());
					return customApplicationProperties.getCorsOrigins().getFrontEndModule();
				case VERIFICATION_TOKEN_CREATION:
					/**
					 * Creating base app URL for:
					 * {@link me.grudzien.patryk.events.registration.RegistrationCompleteListener#createVerificationTokenAndSendEmail(me.grudzien.patryk.events.registration.OnRegistrationCompleteEvent)}
					 * createVerificationTokenAndSendEmail() method in RegistrationCompleteListener
					 *
					 * @return base app URL which is used in (exception-mail) sent to the user.
					 */
					log.info(METHOD_INVOCATION_MARKER, VERIFICATION_TOKEN_CREATION.getLogInfoMessage());
					return customApplicationProperties.getCorsOrigins().getBackEndModule();
				case CONFIRM_REGISTRATION:
					/**
					 * Creating base app URL for:
					 * {@link me.grudzien.patryk.controller.registration.UserRegistrationController#confirmRegistration(String, javax.servlet.http.HttpServletResponse, org.springframework.web.context.request.WebRequest)}
					 *
					 * @return base app URL which is used to redirect user to (confirmation screen) on UI.
					 */
					log.info(METHOD_INVOCATION_MARKER, CONFIRM_REGISTRATION.getLogInfoMessage());
					return customApplicationProperties.getCorsOrigins().getFrontEndModule();
				default:
					log.error(EXCEPTION_MARKER, "Unknown action... Cannot determine app url where user will be redirected...");
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
