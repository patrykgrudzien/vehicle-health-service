package me.grudzien.patryk.repository.vehicle;

import org.springframework.data.repository.CrudRepository;

import me.grudzien.patryk.domain.entities.vehicle.Vehicle;

public interface VehicleRepository extends CrudRepository<Vehicle, Long> {

	Vehicle findByCustomUserId(Long customUserId);

	Vehicle findByCustomUserEmail(String customUserEmail);
}
