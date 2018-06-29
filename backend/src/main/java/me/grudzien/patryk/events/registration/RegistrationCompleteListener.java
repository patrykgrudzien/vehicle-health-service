package me.grudzien.patryk.events.registration;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import java.util.UUID;

import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.dto.registration.EmailDto;
import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.service.registration.EmailService;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.HerokuAppEndpointResolver;

/**
 * That listener is going to handle {@link OnRegistrationCompleteEvent} which is published by
 * {@link CustomUser} object in {@link me.grudzien.patryk.service.registration.UserRegistrationServiceImpl} after saving new user to database.
 */
@Log4j2
@Component
public class RegistrationCompleteListener implements ApplicationListener<OnRegistrationCompleteEvent> {

	private final EmailService emailService;
	private final CustomApplicationProperties customApplicationProperties;
	private final HerokuAppEndpointResolver herokuAppEndpointResolver;
	private final LocaleMessagesCreator localeMessagesCreator;

	@Autowired
	public RegistrationCompleteListener(final EmailService emailService, final CustomApplicationProperties customApplicationProperties,
	                                    final HerokuAppEndpointResolver herokuAppEndpointResolver,
	                                    final LocaleMessagesCreator localeMessagesCreator) {
		this.emailService = emailService;
		this.customApplicationProperties = customApplicationProperties;
		this.herokuAppEndpointResolver = herokuAppEndpointResolver;
		this.localeMessagesCreator = localeMessagesCreator;
		log.info(FLOW_MARKER, "{} bean injected.", HerokuAppEndpointResolver.class);
	}

	@Override
	public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
		createVerificationTokenAndSendEmail(event);
	}

	private void createVerificationTokenAndSendEmail(final OnRegistrationCompleteEvent event) {
		log.info(FLOW_MARKER, "Listener received event: {} with user email: {}", event.getEventName(), event.getCustomUser().getEmail());

		final CustomUser userBeingRegistered = event.getCustomUser();

		final String token = UUID.randomUUID().toString();
		emailService.persistEmailVerificationToken(event.getCustomUser(), token);
		log.info(FLOW_MARKER, "Created token: {}", token);

		final String recipientAddress = userBeingRegistered.getEmail();
		final String subject = localeMessagesCreator.buildLocaleMessage("registration-email-subject");
		final String confirmationUrl = event.getApplicationUrl() + customApplicationProperties.getEndpoints()
		                                                                                      .getRegistration()
		                                                                                      .getRootConfirmationUrl() + token;
		emailService.sendMessageUsingTemplate(EmailDto.Builder()
		                                              .to(recipientAddress)
		                                              .subject(subject)
		                                              .content(customApplicationProperties.getCorsOrigins().getFrontEndModule() + confirmationUrl)
		                                              .templatePlaceholders(
				                                              ImmutableMap.<String, Object>
					                                                 builder()
					                                                .put("userFirstName", userBeingRegistered.getFirstName())
			                                                        .put("confirmationUrl",
			                                                             herokuAppEndpointResolver.determineBaseAppUrlForVerificationToken() + confirmationUrl)
			                                                        .build())
		                                              .build());
		log.info(FLOW_MARKER, "Registration confirmation email has been sent to {}", recipientAddress);
	}
}
