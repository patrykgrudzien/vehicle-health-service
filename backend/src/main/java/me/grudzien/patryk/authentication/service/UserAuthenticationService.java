package me.grudzien.patryk.authentication.service;

import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import me.grudzien.patryk.authentication.exception.BadCredentialsAuthenticationException;
import me.grudzien.patryk.authentication.exception.UserDisabledAuthenticationException;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;
import me.grudzien.patryk.authentication.service.impl.MyUserDetailsService;

/**
 * Service responsible for handling login and authentication actions.
 */
public interface UserAuthenticationService {

    /**
     * Logs in the user to the system and provides access and refresh tokens.
     *
     * @param authenticationRequest Credentials of the user who is being authenticated.
     * @param device A model for the user agent or device that submitted the current request.
     * @return {@link JwtAuthenticationResponse} containing:
     * <ul>
     *     <li>accessToken</li>
     *     <li>refreshToken</li>
     *     <li>isSuccessful</li> <i>flag describing if login either succeeded or failed</i>
     * </ul>
     */
	JwtAuthenticationResponse login(JwtAuthenticationRequest authenticationRequest, Device device);

    /**
     * Authenticates the user. If something is wrong, the
     * {@link BadCredentialsAuthenticationException} or
     * {@link UserDisabledAuthenticationException} will be thrown.
     *
     * <br><br>
     * {@link AuthenticationManager} in {@link AuthenticationManager#authenticate(Authentication)} method
     * will use {@link DaoAuthenticationProvider} which is the implementation of:
     * {@link AuthenticationProvider} interface that receives {@link UserDetails} from:
     * {@link MyUserDetailsService}.
     *
     * <br><br>
     * {@link AuthenticationManager#authenticate(Authentication)} attempts to authenticate the passed
     * {@link Authentication} object and if successful - returns fully authenticated object
     * that contains credentials and granted authorities.
     *
     * @param authenticationRequest Credentials of the user who is being authenticated.
     * @return Optional of {@link Authentication} object.
     */
	Optional<Authentication> authenticateUser(JwtAuthenticationRequest authenticationRequest);
}
