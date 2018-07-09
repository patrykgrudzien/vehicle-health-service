package me.grudzien.patryk.aop.pointcuts;

import org.aspectj.lang.annotation.Pointcut;

/**
 * Used in:
 * {@link me.grudzien.patryk.aop.aspects.LoggingAspect}
 */
public abstract class LoggingAspectPointcuts {

	@Pointcut("execution(* me.grudzien.patryk.service.security.MyUserDetailsService.loadUserByUsername(String))")
	public static void loadUserByUsername() {}

	@Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
	public static void withinRestControllerClasses() {}
}
