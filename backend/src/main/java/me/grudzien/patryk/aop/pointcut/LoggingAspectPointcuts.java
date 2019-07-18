package me.grudzien.patryk.aop.pointcut;

import lombok.NoArgsConstructor;

import org.aspectj.lang.annotation.Pointcut;

import me.grudzien.patryk.aop.aspect.LoggingAspect;

import static lombok.AccessLevel.NONE;

/**
 * Used in {@link LoggingAspect}.
 */
@NoArgsConstructor(access = NONE)
public final class LoggingAspectPointcuts {

	@Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
	public static void withinRestControllerClasses() {}

	@Pointcut("execution(@org.springframework.cache.annotation.Cacheable * *.*(..))")
	public static void executionMethodsAnnotatedByCacheableAnnotation() {}

	@Pointcut("execution(@org.springframework.cache.annotation.CachePut * *.*(..))")
	public static void executionMethodsAnnotatedByCachePutAnnotation() {}

	@Pointcut("execution(@org.springframework.cache.annotation.CacheEvict * *.*(..))")
	public static void executionMethodsAnnotatedByCacheEvictAnnotation() {}
}
