package me.grudzien.patryk.jwt.resource.impl;

import lombok.extern.log4j.Log4j2;

import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.RestController;

import org.apache.commons.lang3.StringUtils;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.jwt.resource.JwtResource;
import me.grudzien.patryk.jwt.service.JwtTokenService;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@Log4j2
@RestController
public class JwtResourceImpl implements JwtResource {

    private final JwtTokenService jwtTokenService;

    public JwtResourceImpl(final JwtTokenService jwtTokenService) {
        checkNotNull(jwtTokenService, "jwtTokenService cannot be null!");
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public ResponseEntity<String> generateAccessToken(final JwtAuthenticationRequest jwtAuthenticationRequest, final Device device) {
        final String accessToken = jwtTokenService.generateAccessToken(jwtAuthenticationRequest, device);
        return StringUtils.isEmpty(accessToken) ? badRequest().build() : ok(accessToken);
    }

    @Override
    public ResponseEntity<String> generateRefreshToken(final JwtAuthenticationRequest jwtAuthenticationRequest, final Device device) {
        final String refreshToken = jwtTokenService.generateRefreshToken(jwtAuthenticationRequest, device);
        return StringUtils.isEmpty(refreshToken) ? badRequest().build() : ok(refreshToken);
    }

    @Override
    public ResponseEntity<String> refreshAccessToken(final JwtAuthenticationRequest jwtAuthenticationRequest, final Device device) {
        final String refreshedAccessToken = jwtTokenService.refreshAccessToken(jwtAuthenticationRequest, device);
        return StringUtils.isEmpty(refreshedAccessToken) ? badRequest().build() : ok(refreshedAccessToken);
    }
}
