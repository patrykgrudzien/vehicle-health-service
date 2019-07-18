package me.grudzien.patryk.utils.factory;

import lombok.NoArgsConstructor;

import me.grudzien.patryk.authentication.model.factory.ExceptionFactory;
import me.grudzien.patryk.jwt.model.factory.JwtAuthResponseFactory;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;
import static lombok.AccessLevel.NONE;

@NoArgsConstructor(access = NONE)
public final class FactoryProvider {

	public static <InputType, ReturnObject> AbstractFactory<InputType, ReturnObject> getFactory(final FactoryType factoryType) {
		//noinspection unchecked
		return (AbstractFactory<InputType, ReturnObject>) Match(factoryType).of(
				Case($(is(FactoryType.EXCEPTION)), ExceptionFactory::new),
				Case($(is(FactoryType.JWT)), JwtAuthResponseFactory::new)
		);
	}
}
