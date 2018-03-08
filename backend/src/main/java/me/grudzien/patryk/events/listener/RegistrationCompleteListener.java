package me.grudzien.patryk.events.listener;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

import me.grudzien.patryk.domain.entities.CustomUser;
import me.grudzien.patryk.events.event.OnRegistrationCompleteEvent;
import me.grudzien.patryk.service.CustomUserService;

/**
 * That listener is going to handle OnRegistrationCompleteEvent which is published by
 * CustomUser object in CustomUserServiceImpl after saving new user to database.
 */
@Log4j
@Component
public class RegistrationCompleteListener implements ApplicationListener<OnRegistrationCompleteEvent> {

	private final CustomUserService customUserService;
	private final JavaMailSender javaMailSender;

	@Autowired
	public RegistrationCompleteListener(final CustomUserService customUserService, final JavaMailSender javaMailSender) {
		this.customUserService = customUserService;
		this.javaMailSender = javaMailSender;
	}

	@Override
	public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
		this.createTokenAndSendEmail(event);
	}

	private void createTokenAndSendEmail(final OnRegistrationCompleteEvent event) {
		log.info(String.format("Listener received event: %1s with user email: %2s", event.getEventName(), event.getCustomUser().getEmail()));

		final CustomUser userBeingRegistered = event.getCustomUser();

		final String token = UUID.randomUUID().toString();
		log.info("Created token: " + token);
		customUserService.createEmailVerificationToken(event.getCustomUser(), token);

		final String recipientAddress = userBeingRegistered.getEmail();
		final String subject = "Registration Confirmation";
		final String confirmationUrl = event.getApplicationUrl() + "registration-confirm?token=" + token;

		final SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(recipientAddress);
		email.setSubject(subject);
		email.setText("http://localhost:8080/" + confirmationUrl);
		javaMailSender.send(email);

		log.info(subject + " email has been sent to " + recipientAddress);
	}
}
