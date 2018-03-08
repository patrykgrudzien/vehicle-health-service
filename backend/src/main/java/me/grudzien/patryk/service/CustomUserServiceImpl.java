package me.grudzien.patryk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.stream.Collectors;

import me.grudzien.patryk.domain.dto.UserRegistrationDto;
import me.grudzien.patryk.domain.entities.CustomUser;
import me.grudzien.patryk.domain.entities.Role;
import me.grudzien.patryk.exceptions.exception.CustomUserValidationException;
import me.grudzien.patryk.repository.UserRepository;

@Service
public class CustomUserServiceImpl implements CustomUserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	@Autowired
	public CustomUserServiceImpl(final UserRepository userRepository, final BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional(readOnly = true)
	public Boolean doesEmailExist(final String email) {
		return userRepository.findByEmail(email) != null;
	}

	@Override
	@Transactional
	public CustomUser registerNewUserAccount(final UserRegistrationDto userRegistrationDto, final BindingResult bindingResult) {
		if (!bindingResult.hasErrors()) {
			final CustomUser customUser = CustomUser.Builder()
			                                        .firstName(userRegistrationDto.getFirstName())
			                                        .lastName(userRegistrationDto.getLastName())
			                                        .email(userRegistrationDto.getEmail())
			                                        .password(passwordEncoder.encode(userRegistrationDto.getPassword()))
			                                        .roles(Collections.singleton(new Role("ROLE_USER")))
			                                        .build();
			return userRepository.save(customUser);
		} else {
			throw new CustomUserValidationException("Cannot save user. Validation errors.",
			                                        bindingResult.getAllErrors()
			                                                     .stream()
			                                                     .map(DefaultMessageSourceResolvable::getDefaultMessage)
			                                                     // I'm checking two fields for email and two for password but there in no need to duplicate the same message
			                                                     .distinct()
			                                                     .collect(Collectors.toList()));
		}
	}
}
