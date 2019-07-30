package me.grudzien.patryk.authentication.resource.impl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import me.grudzien.patryk.authentication.mapping.JwtAuthenticationRequestMapper;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;
import me.grudzien.patryk.authentication.model.dto.JwtUser;
import me.grudzien.patryk.authentication.resource.AuthenticationResource;
import me.grudzien.patryk.authentication.service.UserAuthenticationService;
import me.grudzien.patryk.configuration.properties.ui.CustomUIMessageCodesProperties;
import me.grudzien.patryk.utils.validation.ValidationService;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import static me.grudzien.patryk.utils.web.ObjectDecoder.authRequestDecoder;

@Slf4j
@RestController
public class AuthenticationResourceImpl implements AuthenticationResource {

    private final JwtAuthenticationRequestMapper authRequestMapper;
    private final CustomUIMessageCodesProperties uiMessageCodesProperties;
    private final UserAuthenticationService userAuthenticationService;
    private final ValidationService validationService;

    public AuthenticationResourceImpl(final JwtAuthenticationRequestMapper authRequestMapper,
                                      final CustomUIMessageCodesProperties uiMessageCodesProperties,
                                      final UserAuthenticationService userAuthenticationService,
                                      final ValidationService validationService) {
        checkNotNull(authRequestMapper, "authRequestMapper cannot be null!");
        checkNotNull(uiMessageCodesProperties, "uiMessageCodesProperties cannot be null!");
        checkNotNull(userAuthenticationService, "userAuthenticationService cannot be null!");
        checkNotNull(validationService, "validationService cannot be null!");

        this.authRequestMapper = authRequestMapper;
        this.uiMessageCodesProperties = uiMessageCodesProperties;
        this.userAuthenticationService = userAuthenticationService;
        this.validationService = validationService;
    }

    @Override
    public ResponseEntity<JwtAuthenticationResponse> login(final JwtAuthenticationRequest authenticationRequest,
                                                           final Device device, final WebRequest webRequest) {
        final JwtAuthenticationRequest decodedAuthRequest = authRequestDecoder().apply(authenticationRequest, authRequestMapper);
        validationService.validateWithResult(decodedAuthRequest)
                         .onErrorsSetExceptionMessageCode(uiMessageCodesProperties.getLoginFormValidationErrors());
        final JwtAuthenticationResponse authenticationResponse = userAuthenticationService.login(decodedAuthRequest, device);
        return authenticationResponse.isSuccessful() ?
                ok(authenticationResponse) : badRequest().body(authenticationResponse);
    }

    @Override
    public JwtUser getPrincipalUser(final WebRequest webRequest, final JwtUser jwtUser) {
        return jwtUser;
    }
}
