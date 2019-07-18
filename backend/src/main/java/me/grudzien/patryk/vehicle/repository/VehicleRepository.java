package me.grudzien.patryk.vehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import me.grudzien.patryk.vehicle.model.dto.VehicleDto;
import me.grudzien.patryk.vehicle.model.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	Vehicle findByCustomUser_Id(Long customUserId);

	Vehicle findByCustomUser_Email(String customUserEmail);

	@SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
	@Query("SELECT new me.grudzien.patryk.vehicle.model.dto.VehicleDto(v.vehicleType, v.engine.engineType, v.mileage, v.customUser.id, v.customUser.email) "
	       + "FROM Vehicle v WHERE v.customUser.email = :ownerEmailAddress")
	VehicleDto findDtoByOwnerEmailAddress(@Param("ownerEmailAddress") String ownerEmailAddress);

	@Query("SELECT v.mileage FROM Vehicle v WHERE v.customUser = (SELECT u.id FROM CustomUser u WHERE u.email = :ownerEmailAddress)")
	Long getVehicleCurrentMileage(@Param("ownerEmailAddress") String ownerEmailAddress);

	@Modifying
	@Transactional
	@Query("UPDATE Vehicle v SET v.mileage = :newMileage WHERE v.customUser = "
	       + "(SELECT u.id FROM CustomUser u WHERE u.email = :ownerEmailAddress)")
	void updateCurrentMileage(@Param("newMileage") Long newMileage, @Param("ownerEmailAddress") String ownerEmailAddress);
}
