package me.grudzien.patryk.vehicle.exception;

public class VehicleNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 5599759449247545615L;

	public VehicleNotFoundException(final String message) {
		super(message);
	}
}
