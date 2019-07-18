package me.grudzien.patryk.aop.aspect;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.stream.Stream;

@Log4j2
@Component
@Aspect
public class LoggingAspect {

	/**
	 * This advice is executed before all classes annotated by:
	 * {@link org.springframework.web.bind.annotation.RestController}
	 * annotation.
	 */
	@Before("me.grudzien.patryk.aop.pointcut.LoggingAspectPointcuts.withinRestControllerClasses()")
	public void beforeClassesAnnotatedWithRestControllerAnnotation(final JoinPoint joinPoint) {
		log.info("----- Method -----> {}", joinPoint.getSignature().toShortString());
		Stream.of(joinPoint.getArgs()).forEach(arg -> {
			if (arg instanceof ServletWebRequest) {
				log.info("----- Path -----> {}", ((ServletWebRequest) arg).getRequest().getRequestURI());
			}
		});
	}

	/**
	 * This advice is executed before all methods annotated by:
	 * {@link org.springframework.cache.annotation.Cacheable} OR
	 * {@link org.springframework.cache.annotation.CachePut}  OR
	 * {@link org.springframework.cache.annotation.CacheEvict}
	 * annotations.
	 */
	@Before("me.grudzien.patryk.aop.pointcut.LoggingAspectPointcuts.executionMethodsAnnotatedByCacheableAnnotation() || "
	        + "me.grudzien.patryk.aop.pointcut.LoggingAspectPointcuts.executionMethodsAnnotatedByCachePutAnnotation() || "
	        + "me.grudzien.patryk.aop.pointcut.LoggingAspectPointcuts.executionMethodsAnnotatedByCacheEvictAnnotation()")
	public void beforeMethodsAnnotatedWithCacheAnnotations(final JoinPoint joinPoint) {
		log.info("----- Checking cache. Method -----> {}", joinPoint.getSignature().toShortString());
	}
}
