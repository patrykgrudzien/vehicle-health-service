package me.grudzien.patryk.aop.pointcut;

import org.aspectj.lang.annotation.Pointcut;

/**
 * Used in:
 * {@link me.grudzien.patryk.aop.aspect.LoggingAspect}
 */
public abstract class LoggingAspectPointcuts {

	@Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
	public static void withinRestControllerClasses() {}

	@Pointcut("execution(@org.springframework.cache.annotation.Cacheable * *.*(..))")
	public static void executionMethodsAnnotatedByCacheableAnnotation() {}

	@Pointcut("execution(@org.springframework.cache.annotation.CachePut * *.*(..))")
	public static void executionMethodsAnnotatedByCachePutAnnotation() {}

	@Pointcut("execution(@org.springframework.cache.annotation.CacheEvict * *.*(..))")
	public static void executionMethodsAnnotatedByCacheEvictAnnotation() {}
}
