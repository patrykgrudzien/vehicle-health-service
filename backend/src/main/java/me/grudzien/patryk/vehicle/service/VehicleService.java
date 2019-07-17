package me.grudzien.patryk.vehicle.service;

import me.grudzien.patryk.vehicle.model.dto.VehicleDto;
import me.grudzien.patryk.vehicle.model.entity.Vehicle;

public interface VehicleService {

	/**
	 * customUserId is passed as String because it's encoded on UI side and encoded form is always String type
	 */
	Vehicle findByCustomUserId(String customUserId);

	Vehicle findByCustomUserEmail(String customUserEmail);

	VehicleDto findDtoByOwnerEmailAddress(String ownerEmailAddress);

	Long getVehicleCurrentMileage(String ownerEmailAddress);

	void updateCurrentMileage(String newMileage, String ownerEmailAddress);
}
