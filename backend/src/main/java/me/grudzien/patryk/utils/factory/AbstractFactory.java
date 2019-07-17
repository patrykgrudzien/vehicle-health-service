package me.grudzien.patryk.utils.factory;

@FunctionalInterface
public interface AbstractFactory<InputType, ReturnObject> {

	ReturnObject create(final InputType inputType, final Object... args);
}
