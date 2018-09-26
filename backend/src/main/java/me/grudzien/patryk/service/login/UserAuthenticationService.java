package me.grudzien.patryk.service.login;

import org.springframework.mobile.device.Device;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;

public interface UserAuthenticationService {

	JwtAuthenticationResponse login(final JwtAuthenticationRequest authenticationRequest, final Device device);

	Optional<Authentication> authenticateUser(final JwtAuthenticationRequest authenticationRequest);

	String createRefreshedAuthAccessToken(final JwtAuthenticationRequest authenticationRequest, final Device device);
}
