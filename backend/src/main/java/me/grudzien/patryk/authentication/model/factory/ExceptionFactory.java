package me.grudzien.patryk.authentication.model.factory;

import lombok.extern.log4j.Log4j2;

import java.util.function.Supplier;

import me.grudzien.patryk.utils.factory.AbstractFactory;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.CheckedFunction2.liftTry;
import static io.vavr.Predicates.is;

import static me.grudzien.patryk.authentication.model.factory.ExceptionType.DYNAMIC_BASED_ON_INPUT;
import static me.grudzien.patryk.authentication.model.factory.ExceptionType.UNKNOWN;

@Log4j2
public final class ExceptionFactory implements AbstractFactory<ExceptionType, Throwable> {

	@Override
	public Throwable create(final ExceptionType exceptionType, final Object... args) {
		return Match(exceptionType).of(
				Case($(is(DYNAMIC_BASED_ON_INPUT)), () -> createDynamicRuntimeException(args)),
				Case($(is(UNKNOWN)), (Supplier<? extends RuntimeException>) RuntimeException::new)
		);
	}

    private RuntimeException createDynamicRuntimeException(final Object[] args) {
	    return (RuntimeException) liftTry(
	            (className, stringParam) -> Class.forName((String) className)
                                                 .getConstructor(String.class)
                                                 .newInstance((String) stringParam)
        ).apply(args[0], args[1]).get();
    }
}
