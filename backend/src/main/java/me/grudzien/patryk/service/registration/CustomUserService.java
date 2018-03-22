package me.grudzien.patryk.service.registration;

import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.domain.entities.registration.EmailVerificationToken;

public interface CustomUserService {

	Boolean doesEmailExist(String email);

	void registerNewCustomUserAccount(UserRegistrationDto userRegistrationDto, BindingResult bindingResult, WebRequest webRequest);

	void confirmRegistration(String emailVerificationToken, HttpServletResponse response);

	CustomUser getCustomUser(String emailVerificationToken);

	void saveRegisteredCustomUser(CustomUser customUser);

	void createEmailVerificationToken(CustomUser customUser, String token);

	EmailVerificationToken generateNewEmailVerificationToken(String existingEmailVerificationToken);

	void resendEmailVerificationToken(String existingEmailVerificationToken);

	EmailVerificationToken getEmailVerificationToken(String emailVerificationToken);
}
