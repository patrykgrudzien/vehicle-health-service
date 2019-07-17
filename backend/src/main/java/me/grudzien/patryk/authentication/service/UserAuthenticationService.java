package me.grudzien.patryk.authentication.service;

import org.springframework.mobile.device.Device;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;

public interface UserAuthenticationService {

	JwtAuthenticationResponse login(final JwtAuthenticationRequest authenticationRequest, final Device device);

	Optional<Authentication> authenticateUser(final JwtAuthenticationRequest authenticationRequest);
}
