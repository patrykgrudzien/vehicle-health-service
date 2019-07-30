package me.grudzien.patryk.registration.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.model.entity.EmailVerificationToken;
import me.grudzien.patryk.utils.aop.aspect.LoggingAspect;
import me.grudzien.patryk.utils.web.model.CustomResponse;

import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.CONFIRM_REGISTRATION;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.CREATE_USER_ACCOUNT;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.REGISTRATION_RESOURCE_ROOT;
import static me.grudzien.patryk.registration.resource.RegistrationResourceDefinitions.RESEND_EMAIL_VERIFICATION_TOKEN;

/**
 * Resource that handles registration specific actions.
 */
@RequestMapping(REGISTRATION_RESOURCE_ROOT)
public interface RegistrationResource {

    /**
     * Creates a new user account in the system.
     *
     * @param registrationDto Registration information passed from UI form.
     * @param webRequest Parameter used for the purpose of {@link LoggingAspect}.
     * @return {@link HttpStatus#OK} if creation of a new account was successful,
     * otherwise: {@link HttpStatus#BAD_REQUEST} with {@link CustomResponse} in a body.
     */
    @PostMapping(CREATE_USER_ACCOUNT)
    ResponseEntity<CustomResponse> createUserAccount(@RequestBody UserRegistrationDto registrationDto,
                                                     WebRequest webRequest);

    /**
     * Confirms registration process for newly created account.
     *
     * @param token Path parameter to obtain {@link EmailVerificationToken} needed to confirm registration.
     * @param webRequest Parameter used for the purpose of {@link LoggingAspect}.
     * @return {@link HttpStatus#MOVED_PERMANENTLY} to "redirectionURL" if confirmation went successfully,
     * otherwise: {@link HttpStatus#BAD_REQUEST} with {@link CustomResponse} in a body.
     */
    @GetMapping(CONFIRM_REGISTRATION)
    ResponseEntity<CustomResponse> confirmRegistration(@RequestParam("token") String token,
                                                       WebRequest webRequest);

    /**
     * Re-sends email verification token in case of failure during:
     * {@link RegistrationResource#confirmRegistration(String, WebRequest)}.
     *
     * @param token Path parameter to obtain {@link EmailVerificationToken} needed to confirm registration.
     * @param webRequest Parameter used for the purpose of {@link LoggingAspect}.
     * @return {@link HttpStatus#OK} if resending email verification token was successful,
     * otherwise: {@link HttpStatus#BAD_REQUEST}.
     */
    @GetMapping(RESEND_EMAIL_VERIFICATION_TOKEN)
    ResponseEntity<Void> resendEmailVerificationToken(@RequestParam("token") String token,
                                                      WebRequest webRequest);
}
