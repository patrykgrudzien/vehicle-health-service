package me.grudzien.patryk.registration.service;

import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.model.entity.CustomUser;

public interface UserRegistrationService {

	RegistrationResponse createUserAccount(UserRegistrationDto registrationDto);

    RegistrationResponse confirmRegistration(String tokenRequestParam);

    RegistrationResponse enableRegisteredCustomUser(CustomUser customUser);

    void createEmailVerificationTokenForUser(Long customUserId, String uuidToken);
}
