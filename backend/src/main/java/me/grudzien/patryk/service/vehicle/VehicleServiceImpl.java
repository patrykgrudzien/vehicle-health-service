package me.grudzien.patryk.service.vehicle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import me.grudzien.patryk.domain.entities.vehicle.Vehicle;
import me.grudzien.patryk.repository.vehicle.VehicleRepository;

@Service
public class VehicleServiceImpl implements VehicleService {

	private final VehicleRepository vehicleRepository;

	@Autowired
	public VehicleServiceImpl(final VehicleRepository vehicleRepository) {
		this.vehicleRepository = vehicleRepository;
	}

	@Override
	public Vehicle findByCustomUserId(final Long customUserId) {
		return Optional.ofNullable(vehicleRepository.findByCustomUserId(customUserId))
		               // RuntimeException as temporary solution TODO
		               .orElseThrow(() -> new RuntimeException("No vehicle found for specified userId: " + customUserId));
	}

	@Override
	public Vehicle findByCustomUserEmail(final String customUserEmail) {
		return Optional.ofNullable(vehicleRepository.findByCustomUserEmail(customUserEmail))
		               // RuntimeException as temporary solution TODO
		               .orElseThrow(() -> new RuntimeException("No vehicle found for specified user email: " + customUserEmail));
	}
}
