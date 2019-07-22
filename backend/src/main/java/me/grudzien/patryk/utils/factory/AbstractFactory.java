package me.grudzien.patryk.utils.factory;

@FunctionalInterface
public interface AbstractFactory<InputType extends Enum<?>, ReturnObject> {

	ReturnObject create(final InputType inputType, final Object... args);
}
