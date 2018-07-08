package me.grudzien.patryk.aspects;

import lombok.extern.log4j.Log4j2;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.stream.Stream;

import static me.grudzien.patryk.utils.log.LogMarkers.ASPECT_MARKER;

import me.grudzien.patryk.domain.dto.login.JwtUser;

@Aspect
@Component
@Log4j2
public class LoggingAspect {

	@Pointcut("execution(* me.grudzien.patryk.service.security.MyUserDetailsService.loadUserByUsername(String))")
	private void loadUserByUsername() {}

	@Pointcut("execution(* me.grudzien.patryk.controller.login.UserAuthenticationController.getPrincipalUser())")
	private void getPrincipalUser() {}

	@Before("loadUserByUsername() || getPrincipalUser()")
	public void beforeLoadUserByUsernameOrGetPrincipalUser(final JoinPoint joinPoint) {
		log.info("----------------------------------------------");
		log.info(ASPECT_MARKER, "=====> Method =====> {}", joinPoint.getSignature().toShortString());
		Stream.of(joinPoint.getArgs()).forEach(arg -> {
			if (!StringUtils.isEmpty(arg)) {
				log.info(ASPECT_MARKER, "=====> Method parameter =====> {}", arg);
			}
		});
		log.info("----------------------------------------------");
	}

	@AfterReturning(
			pointcut = "loadUserByUsername()",
			returning = "jwtUser"
	)
	public void afterLoadUserByUsername(final JwtUser jwtUser) {
		log.info("----------------------------------------------");
		log.info(ASPECT_MARKER, "=====> Loaded user name =====> {}", jwtUser.getFirstname());
		log.info("----------------------------------------------");
	}

	@AfterThrowing(
			pointcut = "loadUserByUsername()",
			throwing = "exception"
	)
	public void afterThrowingLoadUserByUsername(final UsernameNotFoundException exception) {
		log.info("----------------------------------------------");
		log.info(ASPECT_MARKER, "=====> Exception message =====> {}", exception.getMessage());
		log.info("----------------------------------------------");
	}
}
