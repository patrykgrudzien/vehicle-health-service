package me.grudzien.patryk.aop.aspects;

import lombok.extern.log4j.Log4j2;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.stream.Stream;

import me.grudzien.patryk.domain.dto.login.JwtUser;

import static me.grudzien.patryk.utils.log.LogMarkers.ASPECT_MARKER;

@Aspect
@Component
@Log4j2
public class LoggingAspect {

	@Before(value = "me.grudzien.patryk.aop.pointcuts.LoggingAspectPointcuts.loadUserByUsername() || me.grudzien.patryk.aop.pointcuts.LoggingAspectPointcuts.getPrincipalUser()")
	public void beforeLoadUserByUsernameOrGetPrincipalUser(final JoinPoint joinPoint) {
		log.info("----------------------------------------------");
		log.info(ASPECT_MARKER, "----- Method =====> {}", joinPoint.getSignature().toShortString());
		Stream.of(joinPoint.getArgs()).forEach(arg -> {
			if (!StringUtils.isEmpty(arg)) {
				log.info(ASPECT_MARKER, "----- Method parameter =====> {}", arg);
			}
		});
		log.info("----------------------------------------------");
	}

	@AfterReturning(
			pointcut = "me.grudzien.patryk.aop.pointcuts.LoggingAspectPointcuts.loadUserByUsername()",
			returning = "jwtUser"
	)
	public void afterLoadUserByUsername(final JwtUser jwtUser) {
		log.info("----------------------------------------------");
		log.info(ASPECT_MARKER, "----- Loaded user name =====> {}", jwtUser.getFirstname());
		log.info("----------------------------------------------");
	}

	@AfterThrowing(
			pointcut = "me.grudzien.patryk.aop.pointcuts.LoggingAspectPointcuts.loadUserByUsername()",
			throwing = "exception"
	)
	public void afterThrowingLoadUserByUsername(final UsernameNotFoundException exception) {
		log.info("----------------------------------------------");
		log.info(ASPECT_MARKER, "----- Exception message =====> {}", exception.getMessage());
		log.info("----------------------------------------------");
	}

	@Around("me.grudzien.patryk.aop.pointcuts.LoggingAspectPointcuts.loadUserByUsername()")
	public Object aroundLoadUserByUsername(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

		log.info("----------------------------------------------");
		final long begin = System.currentTimeMillis();
		final Object result = proceedingJoinPoint.proceed();
		final long end = System.currentTimeMillis();
		log.info(ASPECT_MARKER, "----- Around advice, process duration =====> {} seconds.", ((end - begin) / 1000.0));
		log.info("----------------------------------------------");

		return result;
	}
}
