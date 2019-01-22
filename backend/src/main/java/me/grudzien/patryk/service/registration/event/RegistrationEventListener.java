package me.grudzien.patryk.service.registration.event;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.UUID;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.domain.dto.registration.EmailDto;
import me.grudzien.patryk.domain.entity.registration.CustomUser;
import me.grudzien.patryk.service.registration.EmailClientService;
import me.grudzien.patryk.service.registration.UserRegistrationService;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.util.web.ContextPathsResolver;

import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_CREATION;
import static me.grudzien.patryk.util.log.LogMarkers.FLOW_MARKER;

/**
 * That listener is going to handle {@link RegistrationCompleteEvent} which is published by
 * {@link CustomUser} object in {@link me.grudzien.patryk.service.registration.impl.UserRegistrationServiceImpl} after saving new user to database.
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
		log.info(FLOW_MARKER, "Listener received event: {} with user email: {}", event.getEventName(), event.getCustomUser().getEmail());

		final CustomUser userBeingRegistered = event.getCustomUser();

		final String uuidToken = UUID.randomUUID().toString();
		userRegistrationService.createEmailVerificationTokenForUser(event.getCustomUser(), uuidToken);

		final String recipientAddress = userBeingRegistered.getEmail();
		final String subject = localeMessagesCreator.buildLocaleMessage("registration-email-subject");
		final String confirmationUrl = event.getApplicationUrl() + propertiesKeeper.endpoints().REGISTRATION_CONFIRMATION + uuidToken;

		if (userBeingRegistered.isHasFakeEmail())
			log.info(FLOW_MARKER, "Fake email address. Confirmation has NOT been sent.");
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
			log.info(FLOW_MARKER, "Registration confirmation email has been sent to {}", recipientAddress);
		}
	}
}
