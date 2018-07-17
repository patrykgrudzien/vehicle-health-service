package me.grudzien.patryk.service.login;

import org.springframework.mobile.device.Device;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.enums.jwt.TokenTypes;

public interface UserAuthenticationService {

	String authenticateAndGenerateToken(final TokenTypes tokenType, final JwtAuthenticationRequest authenticationRequest, final Device device);

	String refreshAuthenticationAccessToken(final JwtAuthenticationRequest authenticationRequest, final Device device);
}
