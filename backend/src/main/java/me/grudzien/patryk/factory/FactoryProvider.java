package me.grudzien.patryk.factory;

import me.grudzien.patryk.factory.exception.ExceptionFactory;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

public final class FactoryProvider {

	private FactoryProvider() {
		// hidden
	}

	@SuppressWarnings("SameParameterValue")
	public static <ReturnObject, InputType> AbstractFactory<ReturnObject, InputType> getFactory(final FactoryType factoryType) {
		//noinspection unchecked
		return (AbstractFactory<ReturnObject, InputType>) Match(factoryType).of(
				Case($(is(FactoryType.EXCEPTION)), ExceptionFactory::new)
		);
	}
}
