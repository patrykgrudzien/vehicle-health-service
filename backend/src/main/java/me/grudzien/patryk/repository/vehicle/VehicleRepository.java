package me.grudzien.patryk.repository.vehicle;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import me.grudzien.patryk.domain.dto.vehicle.VehicleDto;
import me.grudzien.patryk.domain.entities.vehicle.Vehicle;

public interface VehicleRepository extends CrudRepository<Vehicle, Long> {

	Vehicle findByCustomUserId(Long customUserId);

	Vehicle findByCustomUserEmail(String customUserEmail);

	@SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
	@Query("SELECT new me.grudzien.patryk.domain.dto.vehicle.VehicleDto(v.vehicleType, v.engine.engineType, v.mileage, v.customUser.id, v.customUser.email) "
	       + "FROM Vehicle v WHERE v.customUser.email = :ownerEmailAddress")
	VehicleDto findDtoByOwnerEmailAddress(@Param("ownerEmailAddress") String ownerEmailAddress);
}
