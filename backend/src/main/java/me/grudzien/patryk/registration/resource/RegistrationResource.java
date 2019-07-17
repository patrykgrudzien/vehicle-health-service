package me.grudzien.patryk.registration.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.utils.web.model.CustomResponse;

import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.CREATE_USER_ACCOUNT;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.REGISTRATION_RESOURCE_ROOT;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.RESEND_EMAIL_VERIFICATION_TOKEN;

/**
 *
 */
@RequestMapping(REGISTRATION_RESOURCE_ROOT)
public interface RegistrationResource {

    /**
     *
     * @param userRegistrationDto
     * @param webRequest
     * @return
     */
    @PostMapping(CREATE_USER_ACCOUNT)
    ResponseEntity<CustomResponse> createUserAccount(@RequestBody UserRegistrationDto userRegistrationDto, WebRequest webRequest);

    /**
     *
     * @param token
     * @param webRequest
     * @return
     */
    @GetMapping(CONFIRM_REGISTRATION)
    ResponseEntity<CustomResponse> confirmRegistration(@RequestParam("token") String token, WebRequest webRequest);

    /**
     *
     * @param token
     * @param webRequest
     * @return
     */
    @GetMapping(RESEND_EMAIL_VERIFICATION_TOKEN)
    ResponseEntity<Void> resendEmailVerificationToken(@RequestParam("token") String token, WebRequest webRequest);
}
