package me.grudzien.patryk.aspects;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import org.aspectj.lang.annotation.Aspect;

@Aspect
@Component
@Log4j2
public class LoggingAspect {

//	@Before("execution(* me.grudzien.patryk.service.security.MyUserDetailsService.loadUserByUsername(String))")
//	public void beforeLoadUserByUsername() {
//		log.debug(LogMarkers.ASPECT_MARKER, "Executing beforeLoadUserByUsername() before loadUserByUsername()");
//	}
}
