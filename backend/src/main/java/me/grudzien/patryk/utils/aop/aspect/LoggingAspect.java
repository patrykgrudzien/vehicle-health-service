package me.grudzien.patryk.utils.aop.aspect;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.stream.Stream;

@Slf4j
@Component
@Aspect
public class LoggingAspect {

	/**
	 * This advice is executed before all classes annotated by:
	 * {@link org.springframework.web.bind.annotation.RestController}
	 * annotation.
	 */
	@Before("me.grudzien.patryk.utils.aop.pointcut.LoggingAspectPointcuts.withinRestControllerClasses()")
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
	@Before("me.grudzien.patryk.utils.aop.pointcut.LoggingAspectPointcuts.executionMethodsAnnotatedByCacheableAnnotation() || "
	        + "me.grudzien.patryk.utils.aop.pointcut.LoggingAspectPointcuts.executionMethodsAnnotatedByCachePutAnnotation() || "
	        + "me.grudzien.patryk.utils.aop.pointcut.LoggingAspectPointcuts.executionMethodsAnnotatedByCacheEvictAnnotation()")
	public void beforeMethodsAnnotatedWithCacheAnnotations(final JoinPoint joinPoint) {
		log.info("----- Checking cache. Method -----> {}", joinPoint.getSignature().toShortString());
	}
}
