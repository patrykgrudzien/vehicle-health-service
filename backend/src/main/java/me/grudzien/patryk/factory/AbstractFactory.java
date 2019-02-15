package me.grudzien.patryk.factory;

public interface AbstractFactory<InputType, ReturnObject> {

	ReturnObject create(final InputType inputType, final String arg1, final String arg2);
}
