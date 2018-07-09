package me.grudzien.patryk.aop.pointcuts;

import org.aspectj.lang.annotation.Pointcut;

public abstract class LoggingAspectPointcuts {

	@Pointcut("execution(* me.grudzien.patryk.service.security.MyUserDetailsService.loadUserByUsername(String))")
	public static void loadUserByUsername() {}

	@Pointcut("execution(* me.grudzien.patryk.controller.login.UserAuthenticationController.getPrincipalUser())")
	public static void getPrincipalUser() {}
}
