package me.grudzien.patryk.registration.model.event;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.UUID;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.registration.model.dto.EmailDto;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.service.EmailClientService;
import me.grudzien.patryk.registration.service.UserRegistrationService;
import me.grudzien.patryk.registration.service.UserRegistrationServiceImpl;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.web.ContextPathsResolver;

import static me.grudzien.patryk.utils.app.AppFLow.VERIFICATION_TOKEN_CREATION;

/**
 * That listener is going to handle {@link RegistrationCompleteEvent} which is published by
 * {@link CustomUser} object in {@link UserRegistrationServiceImpl} after saving new user to database.
 */
@Log4j2
@Component
public class RegistrationEventListener implements ApplicationListener<RegistrationCompleteEvent> {

	private final EmailClientService emailClientService;
	private final ContextPathsResolver contextPathsResolver;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final PropertiesKeeper propertiesKeeper;
	private final UserRegistrationService userRegistrationService;

	@Autowired
	public RegistrationEventListener(final EmailClientService emailClientService, final ContextPathsResolver contextPathsResolver,
                                     final LocaleMessagesCreator localeMessagesCreator, final PropertiesKeeper propertiesKeeper,
                                     final UserRegistrationService userRegistrationService) {
        Preconditions.checkNotNull(emailClientService, "emailClientService cannot be null!");
        Preconditions.checkNotNull(contextPathsResolver, "contextPathsResolver cannot be null!");
        Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
        Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
        Preconditions.checkNotNull(userRegistrationService, "userRegistrationService cannot be null!");

        this.emailClientService = emailClientService;
        this.contextPathsResolver = contextPathsResolver;
        this.localeMessagesCreator = localeMessagesCreator;
        this.propertiesKeeper = propertiesKeeper;
        this.userRegistrationService = userRegistrationService;
    }

	@Override
	public void onApplicationEvent(final RegistrationCompleteEvent event) {
		createVerificationTokenAndSendEmail(event);
	}

	private void createVerificationTokenAndSendEmail(final RegistrationCompleteEvent event) {
		log.info("Listener received event: {} with user email: {}", event.getEventName(), event.getCustomUser().getEmail());

		final CustomUser userBeingRegistered = event.getCustomUser();

		final String uuidToken = UUID.randomUUID().toString();
		userRegistrationService.createEmailVerificationTokenForUser(event.getCustomUser(), uuidToken);

		final String recipientAddress = userBeingRegistered.getEmail();
		final String subject = localeMessagesCreator.buildLocaleMessage("registration-email-subject");
		final String confirmationUrl = event.getApplicationUrl() + propertiesKeeper.endpoints().REGISTRATION_CONFIRMATION + uuidToken;

		if (userBeingRegistered.isHasFakeEmail())
			log.info("Fake email address. Confirmation has NOT been sent.");
		else {
			emailClientService.sendMessageUsingTemplate(EmailDto.Builder()
                                                                .to(recipientAddress)
                                                                .subject(subject)
                                                                .content(propertiesKeeper.corsOrigins().FRONT_END_MODULE + confirmationUrl)
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
