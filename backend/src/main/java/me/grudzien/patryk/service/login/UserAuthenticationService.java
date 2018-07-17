package me.grudzien.patryk.service.login;

import org.springframework.mobile.device.Device;

import javax.servlet.http.HttpServletRequest;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;

public interface UserAuthenticationService {

	String authenticateAndGenerateToken(final JwtAuthenticationRequest authenticationRequest, final Device device);

	String refreshAuthenticationToken(final HttpServletRequest request);
}
