package me.grudzien.patryk.factory;

final class FactoryProvider {

	private FactoryProvider() {
		throw new UnsupportedOperationException("Creating object of this class is not allowed!");
	}

	static AbstractFactory getFactory() {
		return null;
	}
}
