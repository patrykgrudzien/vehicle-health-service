package me.grudzien.patryk.service.registration;

import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.domain.dto.registration.RegistrationResponse;
import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.domain.entity.registration.CustomUser;

public interface UserRegistrationService {

	Boolean doesEmailExist(String email);

	RegistrationResponse registerNewCustomUserAccount(UserRegistrationDto userRegistrationDto, WebRequest webRequest);

	void confirmRegistration(String emailVerificationToken, HttpServletResponse response);

	CustomUser getCustomUserFromEmailVerificationToken(String emailVerificationToken);

	void enableRegisteredCustomUser(CustomUser customUser);

	void resendEmailVerificationToken(String existingEmailVerificationToken);
}
