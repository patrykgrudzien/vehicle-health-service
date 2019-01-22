package me.grudzien.patryk.service.registration;

import me.grudzien.patryk.domain.dto.registration.RegistrationResponse;
import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.domain.entity.registration.CustomUser;
import me.grudzien.patryk.domain.entity.registration.EmailVerificationToken;

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
