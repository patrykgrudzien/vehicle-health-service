package me.grudzien.patryk.factory.exception;

import io.vavr.CheckedFunction2;
import io.vavr.Function2;
import io.vavr.control.Try;

import java.util.function.Supplier;

import me.grudzien.patryk.factory.AbstractFactory;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

public class ExceptionFactory implements AbstractFactory<Throwable, ExceptionType> {

	@Override
	public Throwable create(final ExceptionType exceptionType, final String... params) {
		return Match(exceptionType).of(
				Case($(is(ExceptionType.DYNAMIC)), () -> {
					final Function2<String, String, Try<RuntimeException>> liftTry = CheckedFunction2.liftTry(
							(exceptionClassName, exceptionMessage) -> (RuntimeException) Class.forName(exceptionClassName)
							                                                                  .getConstructor(String.class)
							                                                                  .newInstance(exceptionMessage));
					return liftTry.apply(params[0], params[1]).get();
				}),
				Case($(is(ExceptionType.UNKNOWN)), (Supplier<RuntimeException>) RuntimeException::new)
		);
	}
}
