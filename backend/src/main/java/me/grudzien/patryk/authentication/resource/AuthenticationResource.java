package me.grudzien.patryk.authentication.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtUser;

import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.AUTHENTICATION_RESOURCE_ROOT;
import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.LOGIN;
import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.PRINCIPAL_USER;

/**
 *
 */
@RequestMapping(AUTHENTICATION_RESOURCE_ROOT)
public interface AuthenticationResource {

    /**
     *
     * @param authenticationRequest
     * @param device
     * @param webRequest
     * @return
     */
    @PostMapping(LOGIN)
    ResponseEntity<?> login(@RequestBody JwtAuthenticationRequest authenticationRequest, Device device, WebRequest webRequest);

    /**
     *
     * @param webRequest
     * @param jwtUser
     * @return
     */
    @GetMapping(PRINCIPAL_USER)
    @PreAuthorize("isAuthenticated()")
    JwtUser getPrincipalUser(WebRequest webRequest, @AuthenticationPrincipal JwtUser jwtUser);
}
