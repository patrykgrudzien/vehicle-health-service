package me.grudzien.patryk.service;

import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;

import me.grudzien.patryk.domain.dto.UserRegistrationDto;
import me.grudzien.patryk.domain.entities.CustomUser;
import me.grudzien.patryk.domain.entities.EmailVerificationToken;

public interface CustomUserService {

	Boolean doesEmailExist(String email);

	CustomUser registerNewCustomUserAccount(UserRegistrationDto userRegistrationDto, BindingResult bindingResult, WebRequest webRequest);

	CustomUser getCustomUser(String emailVerificationToken);

	void saveRegisteredCustomUser(CustomUser customUser);

	void createEmailVerificationToken(CustomUser customUser, String token);

	EmailVerificationToken getEmailVerificationToken(String emailVerificationToken);
}
