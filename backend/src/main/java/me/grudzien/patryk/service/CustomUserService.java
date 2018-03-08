package me.grudzien.patryk.service;

import org.springframework.validation.BindingResult;

import me.grudzien.patryk.domain.dto.UserRegistrationDto;
import me.grudzien.patryk.domain.entities.CustomUser;

public interface CustomUserService {

	Boolean doesEmailExist(String email);

	CustomUser registerNewUserAccount(UserRegistrationDto userRegistrationDto, BindingResult bindingResult);
}
