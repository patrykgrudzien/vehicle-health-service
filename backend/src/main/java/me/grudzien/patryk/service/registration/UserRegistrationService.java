package me.grudzien.patryk.service.registration;

import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.domain.entities.registration.CustomUser;

public interface UserRegistrationService {

	Boolean doesEmailExist(String email);

	void registerNewCustomUserAccount(UserRegistrationDto userRegistrationDto, BindingResult bindingResult, WebRequest webRequest);

	void confirmRegistration(String emailVerificationToken, HttpServletResponse response, WebRequest webRequest);

	CustomUser getCustomUserFromEmailVerificationToken(String emailVerificationToken);

	void saveRegisteredCustomUser(CustomUser customUser);

	void resendEmailVerificationToken(String existingEmailVerificationToken);
}
