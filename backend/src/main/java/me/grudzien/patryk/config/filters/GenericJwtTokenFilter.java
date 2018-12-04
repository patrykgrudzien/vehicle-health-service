package me.grudzien.patryk.config.filters;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.base.Preconditions;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.util.jwt.JwtTokenUtil;

import static me.grudzien.patryk.util.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.util.log.LogMarkers.FLOW_MARKER;

/**
 * <hr><br>
 * The Spring Security {@link org.springframework.security.web.FilterChainProxy} dispatches requests to the first chain that matches.
 * A vanilla Spring Boot application with no custom security configuration has a several (call it n) filter chains, where usually n=6.
 * The first (n-1) chains are there just to ignore static resource patterns, like {@linkplain /css/**} and {@linkplain /images/**}, and
 * the error view {@linkplain /error} (the paths can be controlled by the user with {@linkplain security.ignored} from the
 * {@linkplain org.springframework.boot.autoconfigure.security.SecurityProperties} configuration bean).
 * The last chain matches to catch all path {@linkplain /**} and is more active, containing logic for authentication, authorization,
 * exception handling, session handling, header writing, etc. There are a total of 11 filters in this chain by default, but normally
 * it is not necessary for users to concern themselves with which filters are used and when.
 *
 * <br><br>
 *
 * <h2>Note:</h2>
 * <i>The fact that all filters internal to <b>Spring Security</b> are unknown to the container is important, especially in a Spring Boot
 * application, where all {@linkplain @Beans} of type {@linkplain Filter} are registered automatically with the container by default.
 * So if you want to add a custom filter to the security chain, you need to either <b>NOT</b> make it a {@linkplain @Bean} or wrap it in
 * a {@link org.springframework.boot.web.servlet.FilterRegistrationBean} that explicitly disables the container registration.</u>
 *
 * <br><br><hr><br>
 *
 * Another filters:
 * {@link ServletExceptionHandlerFilter},
 * {@link LocaleDeterminerFilter}
 */
@SuppressWarnings("JavadocReference")
@Log4j2
public class GenericJwtTokenFilter extends OncePerRequestFilter {

    private final String tokenHeader;
    private final UserDetailsService userDetailsService;
    private final LocaleMessagesCreator localeMessagesCreator;

    public GenericJwtTokenFilter(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
                                 final PropertiesKeeper propertiesKeeper, final LocaleMessagesCreator localeMessagesCreator) {
        Preconditions.checkNotNull(userDetailsService, "userDetailsService cannot be null!");
        Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
        Preconditions.checkNotNull(propertiesKeeper, "propertiesKeeper cannot be null!");
        this.userDetailsService = userDetailsService;
        this.localeMessagesCreator = localeMessagesCreator;

        this.tokenHeader = propertiesKeeper.jwt().TOKEN_HEADER;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {

        log.info(FLOW_MARKER, "(FILTER) -----> {} ({}) on path -> {}", this.getClass().getSimpleName(), request.getMethod(), request.getRequestURI());

        final String requestHeader = request.getHeader(this.tokenHeader);

        String email = null;
        String accessToken = null;
        if (requestHeader != null && requestHeader.startsWith(JwtTokenUtil.BEARER)) {
            accessToken = requestHeader.substring(JwtTokenUtil.JWT_TOKEN_BEGIN_INDEX);
            email = JwtTokenUtil.Retriever.getUserEmailFromToken(accessToken);
            log.info(FLOW_MARKER, "Authentication will be performed against user email: {}.", email);
        } else {
            log.warn(EXCEPTION_MARKER, "Couldn't find {} string, it will be ignored!", JwtTokenUtil.BEARER.trim());
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug(FLOW_MARKER, "Security context was null, starting authenticating the user...");
            /*
             * It is not compelling necessary to load the use details from the database. You could also store the information
             * in the token and read it from it.
             */
            final Optional<UserDetails> userDetails = Optional.ofNullable(this.userDetailsService.loadUserByUsername(email));
            /*
             * For simple validation it is completely sufficient to just check the token integrity. You don't have to call
             * the database compellingly.
             */
            if (userDetails.isPresent()) {
                if (JwtTokenUtil.Validator.validateAccessToken(accessToken, userDetails.get())) {
                    /*
                     * UsernamePasswordAuthenticationToken- an {@link org.springframework.security.core.Authentication} implementation that is
                     * designed for simple presentation of a username and password.
                     *
                     * Authentication - represents the token for an authentication request or for an authentication principal once the
                     * request has been processed by "AuthenticationManager.authenticate()" method.
                     * Once the request has been authenticated, the "Authentication" will usually be stored in a thread-local
                     * (SecurityContext) managed by the (SecurityContextHolder) by the authentication mechanism which is being used.
                     *
                     * Credentials - they prove that principal is correct. This is usually a password, but could be anything relevant to the
                     * AuthenticationManager. Callers are expected to populate the credentials.
                     */
                    final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails.get(), null,
                                                                                                                       userDetails.get().getAuthorities());
                    /*
                     * Details - store additional details about the authentication request. These might be an IP address, certificate serial number etc.
                     *
                     * WebAuthenticationDetailsSource - implementation of (AuthenticationDetailsSource) which builds the details object from
                     * an "HttpServletRequest" object.
                     */
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    log.info(FLOW_MARKER, "User has been successfully authenticated. Setting security context.");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {
                throw new UsernameNotFoundException(localeMessagesCreator.buildLocaleMessageWithParam("user-not-found-by-email", email));
            }
        }
        filterChain.doFilter(request, response);
    }
}
