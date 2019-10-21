package me.grudzien.patryk.registration.service.event;

import static com.google.common.base.Preconditions.checkNotNull;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import java.util.UUID;

import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.REGISTRATION_RESOURCE_ROOT;
import static me.grudzien.patryk.utils.appplication.AppFLow.VERIFICATION_TOKEN_CREATION;

import me.grudzien.patryk.configuration.properties.cors.CustomCorsProperties;
import me.grudzien.patryk.registration.model.dto.EmailDto;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.model.event.RegistrationCompleteEvent;
import me.grudzien.patryk.registration.service.EmailClientService;
import me.grudzien.patryk.registration.service.UserRegistrationService;
import me.grudzien.patryk.registration.service.impl.UserRegistrationServiceImpl;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.ContextPathsResolver;

/**
 * That listener is going to handle {@link RegistrationCompleteEvent} which is published by
 * {@link CustomUser} object in {@link UserRegistrationServiceImpl} after saving new user to database.
 */
@Slf4j
@Component
public class RegistrationEventListener implements ApplicationListener<RegistrationCompleteEvent> {

	private final EmailClientService emailClientService;
	private final ContextPathsResolver contextPathsResolver;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final UserRegistrationService userRegistrationService;
	private final CustomCorsProperties corsProperties;

	@Autowired
	public RegistrationEventListener(final EmailClientService emailClientService, final ContextPathsResolver contextPathsResolver,
                                     final LocaleMessagesCreator localeMessagesCreator, final UserRegistrationService userRegistrationService,
                                     final CustomCorsProperties corsProperties) {
        checkNotNull(emailClientService, "emailClientService cannot be null!");
        checkNotNull(contextPathsResolver, "contextPathsResolver cannot be null!");
        checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
        checkNotNull(userRegistrationService, "userRegistrationService cannot be null!");
        checkNotNull(corsProperties, "corsProperties cannot be null!");

        this.emailClientService = emailClientService;
        this.contextPathsResolver = contextPathsResolver;
        this.localeMessagesCreator = localeMessagesCreator;
        this.userRegistrationService = userRegistrationService;
        this.corsProperties = corsProperties;
    }

	@Override
	public void onApplicationEvent(final RegistrationCompleteEvent event) {
		createVerificationTokenAndSendEmail(event);
	}

	private void createVerificationTokenAndSendEmail(final RegistrationCompleteEvent event) {
		log.info("Listener received event: {} with user email: {}", event.getEventName(), event.getCustomUser().getEmail());

		final CustomUser userBeingRegistered = event.getCustomUser();

		final String uuidToken = UUID.randomUUID().toString();
		userRegistrationService.createEmailVerificationTokenForUser(event.getCustomUser().getId(), uuidToken);

		final String recipientAddress = userBeingRegistered.getEmail();
		final String subject = localeMessagesCreator.buildLocaleMessage("registration-email-subject");
		// TODO: get back here and refactor
		final String confirmationUrl = event.getApplicationUrl() + REGISTRATION_RESOURCE_ROOT + CONFIRM_REGISTRATION + "?token=" + uuidToken;

		if (userBeingRegistered.isHasFakeEmail())
			log.info("Fake email address. Confirmation has NOT been sent.");
		else {
			emailClientService.sendMessageUsingTemplate(EmailDto.Builder()
                                                                .to(recipientAddress)
                                                                .subject(subject)
                                                                .content(corsProperties.getFrontEndAppLocalHostUrl() + confirmationUrl)
                                                                .templatePlaceholders(
			                                              		ImmutableMap.<String, Object>
					                                                 builder()
					                                                .put("userFirstName", userBeingRegistered.getFirstName())
			                                                        .put("confirmationUrl",
			                                                             contextPathsResolver.determineUrlFor(VERIFICATION_TOKEN_CREATION) + confirmationUrl)
			                                                        .build())
                                                                .build());
			log.info("Registration confirmation email has been sent to {}", recipientAddress);
		}
	}
}
