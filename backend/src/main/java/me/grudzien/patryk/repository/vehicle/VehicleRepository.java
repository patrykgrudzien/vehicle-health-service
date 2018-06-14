package me.grudzien.patryk.repository.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import me.grudzien.patryk.domain.dto.vehicle.VehicleDto;
import me.grudzien.patryk.domain.entities.vehicle.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	Vehicle findByCustomUserId(Long customUserId);

	Vehicle findByCustomUserEmail(String customUserEmail);

	@SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
	@Query("SELECT new me.grudzien.patryk.domain.dto.vehicle.VehicleDto(v.vehicleType, v.engine.engineType, v.mileage, v.customUser.id, v.customUser.email) "
	       + "FROM Vehicle v WHERE v.customUser.email = :ownerEmailAddress")
	VehicleDto findDtoByOwnerEmailAddress(@Param("ownerEmailAddress") String ownerEmailAddress);

	@Modifying
	@Transactional
	@Query("UPDATE Vehicle v SET v.mileage = :currentMileage WHERE v.customUser = "
	       + "(SELECT u.id FROM CustomUser u WHERE u.email = :ownerEmailAddress)")
	void updateCurrentMileage(@Param("currentMileage") Long currentMileage, @Param("ownerEmailAddress") String 	ownerEmailAddress);
}
