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
import me.grudzien.patryk.utils.aop.aspect.LoggingAspect;

import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.AUTHENTICATION_RESOURCE_ROOT;
import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.LOGIN;
import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.PRINCIPAL_USER;

/**
 * Resource that handles authentication specific actions.
 */
@RequestMapping(AUTHENTICATION_RESOURCE_ROOT)
public interface AuthenticationResource {

    /**
     * Logs in a user to the system.
     *
     * @param authenticationRequest Credentials of the user who is being authenticated.
     * @param device A model for the user agent or device that submitted the current request.
     * @param webRequest Parameter used for the purpose of {@link LoggingAspect}.
     * @return {@link org.springframework.http.HttpStatus#OK} if authentication was successful, otherwise:
     * {@link org.springframework.http.HttpStatus#BAD_REQUEST}.
     */
    @PostMapping(LOGIN)
    ResponseEntity<?> login(@RequestBody JwtAuthenticationRequest authenticationRequest, Device device, WebRequest webRequest);

    /**
     * Retrieves current principal user {@link org.springframework.security.core.Authentication#getPrincipal()}.
     *
     * @param webRequest Parameter used for the purpose of {@link LoggingAspect}.
     * @param jwtUser authenticated principal user of type {@link JwtUser}.
     * @return {@link JwtUser}.
     */
    @GetMapping(PRINCIPAL_USER)
    @PreAuthorize("isAuthenticated()")
    JwtUser getPrincipalUser(WebRequest webRequest, @AuthenticationPrincipal JwtUser jwtUser);
}
