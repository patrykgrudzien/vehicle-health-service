package me.grudzien.patryk.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;

import me.grudzien.patryk.domain.dto.UserRegistrationDto;
import me.grudzien.patryk.domain.entities.CustomUser;

public interface CustomUserDetailsService extends UserDetailsService {

	CustomUser findByEmail(String email);

	CustomUser save(UserRegistrationDto userRegistrationDto, BindingResult bindingResult);
}
