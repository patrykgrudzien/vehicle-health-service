package me.grudzien.patryk.domain.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import me.grudzien.patryk.domain.enums.engine.EngineType;
import me.grudzien.patryk.domain.enums.vehicle.VehicleType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {

	private VehicleType vehicleType;
	private EngineType engineType;
	private Long mileage;
	private Long ownerId;
	private String ownerEmailAddress;
}
