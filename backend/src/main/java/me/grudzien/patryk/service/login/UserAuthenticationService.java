package me.grudzien.patryk.service.login;

import org.springframework.mobile.device.Device;
import org.springframework.web.context.request.WebRequest;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;

public interface UserAuthenticationService {

	String authenticateAndGenerateToken(final JwtAuthenticationRequest authenticationRequest, final Device device, final WebRequest webRequest);
}
