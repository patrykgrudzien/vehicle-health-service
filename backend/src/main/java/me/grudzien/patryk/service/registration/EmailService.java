package me.grudzien.patryk.service.registration;

import me.grudzien.patryk.domain.dto.registration.EmailDto;
import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.domain.entities.registration.EmailVerificationToken;

public interface EmailService {

	void sendSimpleMessage(EmailDto emailDto);

	void sendMessageUsingTemplate(EmailDto emailDto);

	void createEmailVerificationToken(CustomUser customUser, String token);

	EmailVerificationToken generateNewEmailVerificationToken(String existingEmailVerificationToken);

	EmailVerificationToken getEmailVerificationToken(String emailVerificationToken);
}
