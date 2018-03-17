package me.grudzien.patryk.service;

import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.domain.dto.UserRegistrationDto;
import me.grudzien.patryk.domain.entities.CustomUser;
import me.grudzien.patryk.domain.entities.EmailVerificationToken;

public interface CustomUserService {

	Boolean doesEmailExist(String email);

	void registerNewCustomUserAccount(UserRegistrationDto userRegistrationDto, BindingResult bindingResult, WebRequest webRequest);

	void confirmRegistration(String emailVerificationToken, final HttpServletResponse response);

	CustomUser getCustomUser(String emailVerificationToken);

	void saveRegisteredCustomUser(CustomUser customUser);

	void createEmailVerificationToken(CustomUser customUser, String token);

	EmailVerificationToken generateNewEmailVerificationToken(String existingEmailVerificationToken);

	void resendEmailVerificationToken(String existingEmailVerificationToken);

	EmailVerificationToken getEmailVerificationToken(String emailVerificationToken);
}
