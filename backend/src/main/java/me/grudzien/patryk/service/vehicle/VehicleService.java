package me.grudzien.patryk.service.vehicle;

import me.grudzien.patryk.domain.dto.vehicle.VehicleDto;
import me.grudzien.patryk.domain.entities.vehicle.Vehicle;

public interface VehicleService {

	/**
	 * customUserId is passed as String because it's encoded on UI side and encoded form is always String type
	 */
	Vehicle findByCustomUserId(String customUserId);

	Vehicle findByCustomUserEmail(String customUserEmail);

	VehicleDto findDtoByOwnerEmailAddress(String ownerEmailAddress);

	Long getVehicleCurrentMileage(String ownerEmailAddress);

	void updateCurrentMileage(String currentMileage, String ownerEmailAddress);
}
