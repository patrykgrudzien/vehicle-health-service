package me.grudzien.patryk.events.registration;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.UUID;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.dto.registration.EmailDto;
import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.service.registration.EmailService;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.ContextPathsResolver;

import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_CREATION;
import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;

/**
 * That listener is going to handle {@link OnRegistrationCompleteEvent} which is published by
 * {@link CustomUser} object in {@link me.grudzien.patryk.service.registration.impl.UserRegistrationServiceImpl} after saving new user to database.
 */
@Log4j2
@Component
public class RegistrationCompleteListener implements ApplicationListener<OnRegistrationCompleteEvent> {

	private final EmailService emailService;
	private final CustomApplicationProperties customApplicationProperties;
	private final ContextPathsResolver contextPathsResolver;
	private final LocaleMessagesCreator localeMessagesCreator;

	@Autowired
	public RegistrationCompleteListener(final EmailService emailService, final CustomApplicationProperties customApplicationProperties,
	                                    final ContextPathsResolver contextPathsResolver,
	                                    final LocaleMessagesCreator localeMessagesCreator) {
		Preconditions.checkNotNull(emailService, "emailService cannot be null!");
		Preconditions.checkNotNull(customApplicationProperties, "customApplicationProperties cannot be null!");
		Preconditions.checkNotNull(contextPathsResolver, "contextPathsResolver cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");

		this.emailService = emailService;
		this.customApplicationProperties = customApplicationProperties;
		this.contextPathsResolver = contextPathsResolver;
		this.localeMessagesCreator = localeMessagesCreator;
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
			                                                             contextPathsResolver.determineUrlFor(VERIFICATION_TOKEN_CREATION) + confirmationUrl)
			                                                        .build())
		                                              .build());
		log.info(FLOW_MARKER, "Registration confirmation email has been sent to {}", recipientAddress);
	}
}
