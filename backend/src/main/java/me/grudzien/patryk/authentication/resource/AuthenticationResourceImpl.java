package me.grudzien.patryk.authentication.resource;

import lombok.extern.log4j.Log4j2;

import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import me.grudzien.patryk.authentication.mapping.JwtAuthenticationRequestMapper;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;
import me.grudzien.patryk.authentication.model.dto.JwtUser;
import me.grudzien.patryk.authentication.service.UserAuthenticationService;
import me.grudzien.patryk.utils.validation.ValidationService;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import static me.grudzien.patryk.utils.mapping.ObjectDecoder.authRequestDecoder;

@Log4j2
@RestController
public class AuthenticationResourceImpl implements AuthenticationResource {

    private final UserAuthenticationService userAuthenticationService;
    private final JwtAuthenticationRequestMapper jwtAuthenticationRequestMapper;
    private final ValidationService validationService;

    public AuthenticationResourceImpl(final UserAuthenticationService userAuthenticationService,
                                      final JwtAuthenticationRequestMapper jwtAuthenticationRequestMapper,
                                      final ValidationService validationService) {
        checkNotNull(userAuthenticationService, "userAuthenticationService cannot be null!");
        checkNotNull(jwtAuthenticationRequestMapper, "jwtAuthenticationRequestMapper cannot be null!");
        checkNotNull(validationService, "validationService cannot be null!");

        this.userAuthenticationService = userAuthenticationService;
        this.jwtAuthenticationRequestMapper = jwtAuthenticationRequestMapper;
        this.validationService = validationService;
    }

    @Override
    public ResponseEntity<?> login(final JwtAuthenticationRequest authenticationRequest, final Device device, final WebRequest webRequest) {
        // TODO:
        validationService.validate(authenticationRequest, authRequestDecoder(), jwtAuthenticationRequestMapper);
        final JwtAuthenticationResponse authenticationResponse = userAuthenticationService.login(authenticationRequest, device);
        return authenticationResponse.isSuccessful() ?
                ok(authenticationResponse) : badRequest().body(authenticationResponse);
    }

    @Override
    public JwtUser getPrincipalUser(final WebRequest webRequest, final JwtUser jwtUser) {
        return jwtUser;
    }
}
