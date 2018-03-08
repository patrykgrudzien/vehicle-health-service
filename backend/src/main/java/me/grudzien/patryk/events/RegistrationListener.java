package me.grudzien.patryk.events;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

import me.grudzien.patryk.service.CustomUserService;

/**
 * That listener is going to handle OnRegistrationCompleteEvent which is published by
 * CustomUser object in CustomUserServiceImpl after saving new user to database.
 */
@Log4j
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

	private final CustomUserService customUserService;

	// JavaMailSender to be added (remember to provide configuration in application.yml)

	@Autowired
	public RegistrationListener(final CustomUserService customUserService) {
		this.customUserService = customUserService;
	}

	@Override
	public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
		this.createTokenAndSendEmail(event);
	}

	private void createTokenAndSendEmail(final OnRegistrationCompleteEvent event) {
		log.info(String.format("Listener received event: %1s with user email: %2s", event.getEventName(), event.getCustomUser().getEmail()));

		final String token = UUID.randomUUID().toString();
		customUserService.createEmailVerificationToken(event.getCustomUser(), token);

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> SENDING EMAIL TO BE DONE...");
	}
}
