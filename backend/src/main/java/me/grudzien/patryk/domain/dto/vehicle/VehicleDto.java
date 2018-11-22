package me.grudzien.patryk.domain.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

import me.grudzien.patryk.domain.enums.engine.EngineType;
import me.grudzien.patryk.domain.enums.vehicle.VehicleType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class VehicleDto {

	private VehicleType vehicleType;
	private EngineType engineType;
	private Long mileage;
	private Long ownerId;
	private String ownerEmailAddress;
	private String encodedMileage;

	/**
	 * This constructor is needed by:
	 * {@link me.grudzien.patryk.repository.vehicle.VehicleRepository#findDtoByOwnerEmailAddress(String)}
	 * because Lombok provided all args constructor
	 */
	public VehicleDto(final VehicleType vehicleType, final EngineType engineType, final Long mileage, final Long ownerId, final String ownerEmailAddress) {
		this.vehicleType = vehicleType;
		this.engineType = engineType;
		this.mileage = mileage;
		this.ownerId = ownerId;
		this.ownerEmailAddress = ownerEmailAddress;
	}
}
