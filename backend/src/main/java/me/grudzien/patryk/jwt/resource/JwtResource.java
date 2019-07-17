package me.grudzien.patryk.jwt.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;

import static me.grudzien.patryk.jwt.resource.JwtResourceDefinitions.GENERATE_ACCESS_TOKEN;
import static me.grudzien.patryk.jwt.resource.JwtResourceDefinitions.GENERATE_REFRESH_TOKEN;
import static me.grudzien.patryk.jwt.resource.JwtResourceDefinitions.JWT_RESOURCE_ROOT;
import static me.grudzien.patryk.jwt.resource.JwtResourceDefinitions.REFRESH_ACCESS_TOKEN;

/**
 *
 */
@RequestMapping(JWT_RESOURCE_ROOT)
public interface JwtResource {

    /**
     *
     * @param jwtAuthenticationRequest
     * @param device
     * @return
     */
    @PostMapping(GENERATE_ACCESS_TOKEN)
    ResponseEntity<String> generateAccessToken(@RequestBody JwtAuthenticationRequest jwtAuthenticationRequest, Device device);

    /**
     *
     * @param jwtAuthenticationRequest
     * @param device
     * @return
     */
    @PostMapping(GENERATE_REFRESH_TOKEN)
    ResponseEntity<String> generateRefreshToken(@RequestBody JwtAuthenticationRequest jwtAuthenticationRequest, Device device);

    /**
     *
     * @param jwtAuthenticationRequest
     * @param device
     * @return
     */
    @PostMapping(REFRESH_ACCESS_TOKEN)
    ResponseEntity<String> refreshAccessToken(@RequestBody JwtAuthenticationRequest jwtAuthenticationRequest, Device device);
}
