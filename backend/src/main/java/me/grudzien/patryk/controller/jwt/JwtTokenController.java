package me.grudzien.patryk.controller.jwt;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.service.jwt.JwtTokenService;

@Log4j2
@RestController
@RequestMapping("${custom.properties.endpoints.api-context-path}")
public class JwtTokenController {

    private final JwtTokenService jwtTokenService;

    @Autowired
    public JwtTokenController(final JwtTokenService jwtTokenService) {
        Preconditions.checkNotNull(jwtTokenService, "jwtTokenService cannot be null!");
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("${custom.properties.endpoints.authentication.generate-access-token}")
    public ResponseEntity<String> generateAccessToken(@RequestBody final JwtAuthenticationRequest jwtAuthenticationRequest, final Device device) {
        final String accessToken = jwtTokenService.generateAccessToken(jwtAuthenticationRequest, device);
        return StringUtils.isEmpty(accessToken) ?
                ResponseEntity.badRequest().build() : ResponseEntity.ok(accessToken);
    }

    @PostMapping("${custom.properties.endpoints.authentication.generate-refresh-token}")
    public ResponseEntity<String> generateRefreshToken(@RequestBody final JwtAuthenticationRequest jwtAuthenticationRequest, final Device device) {
        final String refreshToken = jwtTokenService.generateRefreshToken(jwtAuthenticationRequest, device);
        return StringUtils.isEmpty(refreshToken) ?
                ResponseEntity.badRequest().build() : ResponseEntity.ok(refreshToken);
    }

    @PostMapping("${custom.properties.endpoints.authentication.refresh-access-token}")
    public ResponseEntity<String> refreshAccessToken(@RequestBody final JwtAuthenticationRequest jwtAuthenticationRequest, final Device device) {
        final String refreshedAccessToken = jwtTokenService.refreshAccessToken(jwtAuthenticationRequest, device);
        return StringUtils.isEmpty(refreshedAccessToken) ?
                ResponseEntity.badRequest().build() : ResponseEntity.ok(refreshedAccessToken);
    }
}
