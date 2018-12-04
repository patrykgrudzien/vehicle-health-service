package me.grudzien.patryk.service.security;

import org.springframework.mobile.device.Device;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;

public interface JwtTokenService {

    String generateAccessToken(JwtAuthenticationRequest authenticationRequest, Device device);

    String generateRefreshToken(JwtAuthenticationRequest authenticationRequest, Device device);

    String refreshAccessToken(JwtAuthenticationRequest authenticationRequest, Device device);
}
