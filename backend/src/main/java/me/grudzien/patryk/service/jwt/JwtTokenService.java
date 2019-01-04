package me.grudzien.patryk.service.jwt;

import org.springframework.mobile.device.Device;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.oauth2.domain.CustomOAuth2OidcPrincipalUser;

public interface JwtTokenService {

    String generateAccessToken(JwtAuthenticationRequest authenticationRequest, Device device);

    String generateAccessTokenCustomExpiration(JwtAuthenticationRequest authenticationRequest, Device device, long minutesToExpire);

    String generateRefreshToken(JwtAuthenticationRequest authenticationRequest, Device device);

    String refreshAccessToken(JwtAuthenticationRequest authenticationRequest, Device device);

    String generateShortLivedToken(CustomOAuth2OidcPrincipalUser customOAuth2OidcPrincipalUser);
}
