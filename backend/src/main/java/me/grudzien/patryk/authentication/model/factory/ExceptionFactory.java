package me.grudzien.patryk.authentication.model.factory;

import io.vavr.CheckedFunction2;
import io.vavr.Function2;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import java.util.function.Supplier;

import me.grudzien.patryk.utils.factory.AbstractFactory;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

@Log4j2
public final class ExceptionFactory implements AbstractFactory<ExceptionType, Throwable> {

	@Override
	public Throwable create(final ExceptionType exceptionType, final Object... args) {
		return Match(exceptionType).of(
				Case($(is(ExceptionType.DYNAMIC_BASED_ON_INPUT)), () -> {
					final Function2<String, String, Try<RuntimeException>> liftTry = CheckedFunction2.liftTry(
							(className, stringParam) -> (RuntimeException) Class.forName(className)
                                                                                .getConstructor(String.class)
                                                                                .newInstance(stringParam));
					final String exceptionClassName = (String) args[0];
					final String exceptionMessage = (String) args[1];
					return liftTry.apply(exceptionClassName, exceptionMessage).get();
				}),
				Case($(is(ExceptionType.UNKNOWN)), (Supplier<RuntimeException>) RuntimeException::new)
		);
	}
}
