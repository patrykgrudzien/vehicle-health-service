package me.grudzien.patryk.service.registration;

import org.springframework.web.context.request.WebRequest;

import me.grudzien.patryk.domain.dto.registration.EmailDto;
import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.domain.entities.registration.EmailVerificationToken;

public interface EmailService {

	void sendSimpleMessage(EmailDto emailDto);

	void sendMessageUsingTemplate(EmailDto emailDto, WebRequest webRequest);

	void persistEmailVerificationToken(CustomUser customUser, String token);

	EmailVerificationToken generateNewEmailVerificationToken(String existingEmailVerificationToken);

	EmailVerificationToken getEmailVerificationToken(String emailVerificationToken);
}
