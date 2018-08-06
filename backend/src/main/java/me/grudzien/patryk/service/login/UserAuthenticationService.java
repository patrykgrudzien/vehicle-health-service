package me.grudzien.patryk.service.login;

import org.springframework.mobile.device.Device;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.domain.enums.jwt.TokenTypes;

public interface UserAuthenticationService {

	JwtAuthenticationResponse login(final JwtAuthenticationRequest authenticationRequest, final Device device);

	Optional<Authentication> authenticateUser(final JwtAuthenticationRequest authenticationRequest);

	String generateToken(final TokenTypes tokenType, final JwtUser jwtUser, final Device device);

	String createRefreshedAuthAccessToken(final JwtAuthenticationRequest authenticationRequest, final Device device);
}
