package me.grudzien.patryk.event.registration;

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
import me.grudzien.patryk.service.registration.EmailService;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.util.web.ContextPathsResolver;

import static me.grudzien.patryk.domain.enums.AppFLow.VERIFICATION_TOKEN_CREATION;
import static me.grudzien.patryk.util.log.LogMarkers.FLOW_MARKER;

/**
 * That listener is going to handle {@link OnRegistrationCompleteEvent} which is published by
 * {@link CustomUser} object in {@link me.grudzien.patryk.service.registration.impl.UserRegistrationServiceImpl} after saving new user to database.
 */
@Log4j2
@Component
public class RegistrationCompleteListener implements ApplicationListener<OnRegistrationCompleteEvent> {

	private final EmailService emailService;
	private final ContextPathsResolver contextPathsResolver;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final PropertiesKeeper propertiesKeeper;

	@Autowired
	public RegistrationCompleteListener(final EmailService emailService, final ContextPathsResolver contextPathsResolver,
	                                    final LocaleMessagesCreator localeMessagesCreator, final PropertiesKeeper propertiesKeeper) {
		Preconditions.checkNotNull(emailService, "emailService cannot be null!");
		Preconditions.checkNotNull(contextPathsResolver, "contextPathsResolver cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");

		this.emailService = emailService;
		this.contextPathsResolver = contextPathsResolver;
		this.localeMessagesCreator = localeMessagesCreator;
		this.propertiesKeeper = propertiesKeeper;
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
		final String confirmationUrl = event.getApplicationUrl() + propertiesKeeper.endpoints().REGISTRATION_CONFIRMATION + token;

		emailService.sendMessageUsingTemplate(EmailDto.Builder()
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
