package me.grudzien.patryk.utils.factory;

import lombok.NoArgsConstructor;

import me.grudzien.patryk.authentication.model.factory.ExceptionFactory;
import me.grudzien.patryk.authentication.model.factory.JwtAuthRequestFactory;
import me.grudzien.patryk.jwt.model.factory.JwtAuthResponseFactory;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;
import static lombok.AccessLevel.NONE;

import static me.grudzien.patryk.utils.factory.FactoryProvider.FactoryType.EXCEPTION_FACTORY;
import static me.grudzien.patryk.utils.factory.FactoryProvider.FactoryType.JWT_AUTH_REQUEST_FACTORY;
import static me.grudzien.patryk.utils.factory.FactoryProvider.FactoryType.JWT_AUTH_RESPONSE_FACTORY;

@NoArgsConstructor(access = NONE)
public final class FactoryProvider {

	@SuppressWarnings("unchecked")
    public static <InputType extends Enum<?>, ReturnObject> AbstractFactory<InputType, ReturnObject> getFactory(final FactoryType factoryType) {
		return (AbstractFactory<InputType, ReturnObject>) Match(factoryType).of(
				Case($(is(EXCEPTION_FACTORY)), ExceptionFactory::new),
				Case($(is(JWT_AUTH_REQUEST_FACTORY)), JwtAuthRequestFactory::new),
				Case($(is(JWT_AUTH_RESPONSE_FACTORY)), JwtAuthResponseFactory::new)
		);
	}

    public enum FactoryType {
        EXCEPTION_FACTORY,
        JWT_AUTH_REQUEST_FACTORY,
        JWT_AUTH_RESPONSE_FACTORY
    }
}
