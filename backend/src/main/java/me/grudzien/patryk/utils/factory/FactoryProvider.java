package me.grudzien.patryk.utils.factory;

import lombok.NoArgsConstructor;

import me.grudzien.patryk.authentication.model.factory.ExceptionFactory;
import me.grudzien.patryk.jwt.model.factory.JwtAuthResponseFactory;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;
import static lombok.AccessLevel.NONE;

import static me.grudzien.patryk.utils.factory.FactoryType.EXCEPTION;
import static me.grudzien.patryk.utils.factory.FactoryType.JWT;

@NoArgsConstructor(access = NONE)
public final class FactoryProvider {

	@SuppressWarnings("unchecked")
    public static <InputType extends Enum<?>, ReturnObject> AbstractFactory<InputType, ReturnObject> getFactory(final FactoryType factoryType) {
		return (AbstractFactory<InputType, ReturnObject>) Match(factoryType).of(
				Case($(is(EXCEPTION)), ExceptionFactory::new),
				Case($(is(JWT)), JwtAuthResponseFactory::new)
		);
	}
}
