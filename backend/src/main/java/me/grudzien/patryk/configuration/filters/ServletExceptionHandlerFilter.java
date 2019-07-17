package me.grudzien.patryk.configuration.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.utils.web.model.CustomResponse.SecurityStatus;
import me.grudzien.patryk.utils.web.model.ExceptionResponse;
import me.grudzien.patryk.jwt.service.impl.JwtTokenClaimsRetrieverImpl;
import me.grudzien.patryk.utils.web.HttpResponseCustomizer;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;
import static io.vavr.Predicates.instanceOf;

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
 * {@link GenericJwtTokenFilter},
 * {@link LocaleDeterminerFilter}
 *
 * This filter is used in case of unsuccessful JWT operations which are performed in:
 * {@link JwtTokenClaimsRetrieverImpl}
 * e.g. when token is expired there is no option to
 * {@link JwtTokenClaimsRetrieverImpl#getAllClaimsFromToken(String)}.
 * In such case one of the exceptions listed below is thrown.
 */
@SuppressWarnings("JavadocReference")
@Log4j2
public class ServletExceptionHandlerFilter extends OncePerRequestFilter {

	@SuppressWarnings("NullableProblems")
    @Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) {

		log.info("(FILTER) -----> {} ({}) on path -> {}", this.getClass().getSimpleName(), request.getMethod(), request.getRequestURI());

		Try.run(() -> filterChain.doFilter(request, response))
		   .onFailure(throwable -> Match(throwable).of(
		   		Case($(instanceOf(ExpiredJwtException.class)), ExpiredJwtException -> run(() -> {
				    log.error("The JWT token is expired and not valid anymore! Message -> {}", ExpiredJwtException.getMessage());
                    HttpResponseCustomizer.customizeHttpResponse(response, HttpStatus.UNAUTHORIZED,
                                                                 ExceptionResponse.buildBodyMessage(ExpiredJwtException, SecurityStatus.JWT_TOKEN_EXPIRED,
                                                                                                    request.getRequestURI(), request.getMethod()));
			    })),
			    Case($(instanceOf(IllegalArgumentException.class)), IllegalArgumentException -> run(() -> {
                    log.error("An error occurred during getting email from token! Message -> {}", IllegalArgumentException.getMessage());
                    HttpResponseCustomizer.customizeHttpResponse(response, HttpStatus.INTERNAL_SERVER_ERROR,
                                                                 ExceptionResponse.buildBodyMessage(IllegalArgumentException, SecurityStatus.ILLEGAL_ARGUMENT,
                                                                                                    request.getRequestURI(), request.getMethod()));
                })),
			    Case($(instanceOf(UnsupportedJwtException.class)), UnsupportedJwtException -> run(() -> {
                    log.error("Application requires JWT token with cryptographically signed Claims! Message -> {}", UnsupportedJwtException.getMessage());
                    HttpResponseCustomizer.customizeHttpResponse(response, HttpStatus.NOT_ACCEPTABLE,
                                                                 ExceptionResponse.buildBodyMessage(UnsupportedJwtException, SecurityStatus.NO_CRYPTOGRAPHICALLY_SIGNED_TOKEN,
                                                                                                    request.getRequestURI(), request.getMethod()));
                })),
			    Case($(instanceOf(MalformedJwtException.class)), MalformedJwtException -> run(() -> {
                    log.error("JWT token has NOT been correctly constructed! Message -> {}", MalformedJwtException.getMessage());
                    HttpResponseCustomizer.customizeHttpResponse(response, HttpStatus.NOT_ACCEPTABLE,
                                                                 ExceptionResponse.buildBodyMessage(MalformedJwtException, SecurityStatus.JWT_TOKEN_NOT_CORRECTLY_CONSTRUCTED,
                                                                                                    request.getRequestURI(), request.getMethod()));
                })),
			    Case($(instanceOf(SignatureException.class)), SignatureException -> run(() -> {
                    log.error("Either calculating a signature or verifying an existing signature of a JWT failed! Message -> {}", SignatureException.getMessage());
                    HttpResponseCustomizer.customizeHttpResponse(response, HttpStatus.INTERNAL_SERVER_ERROR,
                                                                 ExceptionResponse.buildBodyMessage(SignatureException, SecurityStatus.JWT_TOKEN_INCORRECT_SIGNATURE,
                                                                                                    request.getRequestURI(), request.getMethod()));
                })),
			    // if any other exception
			    Case($(), () -> new RuntimeException("No specific exception caught inside ServletExceptionHandlerFilter.doFilterInternal()..."))
		   ));
	}
}
