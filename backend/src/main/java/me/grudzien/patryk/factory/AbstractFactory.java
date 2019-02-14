package me.grudzien.patryk.factory;

public interface AbstractFactory<ReturnObject, InputType> {

	default ReturnObject create(final InputType inputType) {
		// don't force to implement
		return null;
	}

	default ReturnObject create(final InputType inputType, final String... params) {
		// don't force to implement
		return null;
	}
}
