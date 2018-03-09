package me.grudzien.patryk.events.listener;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import java.util.UUID;

import me.grudzien.patryk.domain.dto.EmailDto;
import me.grudzien.patryk.domain.entities.CustomUser;
import me.grudzien.patryk.events.event.OnRegistrationCompleteEvent;
import me.grudzien.patryk.service.CustomUserService;
import me.grudzien.patryk.service.EmailService;

/**
 * That listener is going to handle OnRegistrationCompleteEvent which is published by
 * CustomUser object in CustomUserServiceImpl after saving new user to database.
 */
@Log4j
@Component
public class RegistrationCompleteListener implements ApplicationListener<OnRegistrationCompleteEvent> {

	private final CustomUserService customUserService;
	private final EmailService emailService;

	@Autowired
	public RegistrationCompleteListener(final CustomUserService customUserService, final EmailService emailService) {
		this.customUserService = customUserService;
		this.emailService = emailService;
	}

	@Override
	public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
		createVerificationTokenAndSendEmail(event);
	}

	private void createVerificationTokenAndSendEmail(final OnRegistrationCompleteEvent event) {
		log.info(String.format("Listener received event: %1s with user email: %2s", event.getEventName(), event.getCustomUser().getEmail()));

		final CustomUser userBeingRegistered = event.getCustomUser();

		final String token = UUID.randomUUID().toString();
		log.info("Created token: " + token);
		customUserService.createEmailVerificationToken(event.getCustomUser(), token);

		final String recipientAddress = userBeingRegistered.getEmail();
		final String subject = "Registration Confirmation";
		final String confirmationUrl = event.getApplicationUrl() + "/registration/confirm?token=" + token;

		emailService.sendMessageUsingTemplate(EmailDto.Builder()
		                                              .to(recipientAddress)
		                                              .subject(subject)
		                                              .content("http://localhost:8080/" + confirmationUrl)
		                                              .templatePlaceholders(
				                                              ImmutableMap.<String, Object>
					                                                 builder()
					                                                .put("userFirstName", userBeingRegistered.getFirstName())
			                                                        .put("confirmationUrl", "http://localhost:8080" + confirmationUrl)
			                                                        .build())
		                                              .build());

		log.info(subject + " email has been sent to " + recipientAddress);
	}
}
