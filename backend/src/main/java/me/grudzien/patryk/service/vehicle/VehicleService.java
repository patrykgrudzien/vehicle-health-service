package me.grudzien.patryk.service.vehicle;

import me.grudzien.patryk.domain.dto.vehicle.VehicleDto;
import me.grudzien.patryk.domain.entities.vehicle.Vehicle;

public interface VehicleService {

	Vehicle findByCustomUserId(Long customUserId);

	Vehicle findByCustomUserEmail(String customUserEmail);

	VehicleDto findDtoByOwnerEmailAddress(String ownerEmailAddress);
}
