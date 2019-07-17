package me.grudzien.patryk.registration.service;

import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.model.entity.EmailVerificationToken;

public interface UserRegistrationService {

	Boolean doesEmailExist(String email);

	RegistrationResponse registerNewCustomUserAccount(UserRegistrationDto userRegistrationDto);

    RegistrationResponse confirmRegistration(String tokenRequestParam);

	CustomUser getCustomUserFromEmailVerificationToken(String tokenRequestParam);

    RegistrationResponse enableRegisteredCustomUser(CustomUser customUser);

    void createEmailVerificationTokenForUser(CustomUser customUser, final String uuidToken);

    EmailVerificationToken generateNewEmailVerificationToken(String existingTokenRequestParam);

    EmailVerificationToken getEmailVerificationToken(String tokenRequestParam);
}
