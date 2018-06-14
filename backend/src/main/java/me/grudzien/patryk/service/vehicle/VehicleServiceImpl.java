package me.grudzien.patryk.service.vehicle;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import java.util.Optional;

import me.grudzien.patryk.domain.dto.vehicle.VehicleDto;
import me.grudzien.patryk.domain.entities.vehicle.Vehicle;
import me.grudzien.patryk.exceptions.vehicle.VehicleNotFoundException;
import me.grudzien.patryk.repository.vehicle.VehicleRepository;
import me.grudzien.patryk.utils.web.RequestsDecoder;

@Log4j2
@Service
public class VehicleServiceImpl implements VehicleService {

	private final VehicleRepository vehicleRepository;
	private final RequestsDecoder requestsDecoder;

	@Autowired
	public VehicleServiceImpl(final VehicleRepository vehicleRepository, final RequestsDecoder requestsDecoder) {

		Preconditions.checkNotNull(vehicleRepository, "vehicleRepository cannot be null!");
		Preconditions.checkNotNull(requestsDecoder, "requestsDecoder cannot be null!");

		this.vehicleRepository = vehicleRepository;
		this.requestsDecoder = requestsDecoder;
	}

	@Override
	public Vehicle findByCustomUserId(final String customUserId) {
		final Long decodedCustomUserId = requestsDecoder.decodeStringParamAndConvertToLong(customUserId);
		return Optional.ofNullable(vehicleRepository.findByCustomUserId(decodedCustomUserId))
		               .orElseThrow(() -> new VehicleNotFoundException("No vehicle found for specified userId: " + decodedCustomUserId));
	}

	@Override
	public Vehicle findByCustomUserEmail(final String customUserEmail) {
		final String decodedCustomUserEmail = requestsDecoder.decodeStringParam(customUserEmail);
		return Optional.ofNullable(vehicleRepository.findByCustomUserEmail(decodedCustomUserEmail))
		               .orElseThrow(() -> new VehicleNotFoundException("No vehicle found for specified user email: " + decodedCustomUserEmail));
	}

	@Override
	public VehicleDto findDtoByOwnerEmailAddress(final String ownerEmailAddress) {
		final String decodedOwnerEmailAddress = requestsDecoder.decodeStringParam(ownerEmailAddress);
		return Optional.ofNullable(vehicleRepository.findDtoByOwnerEmailAddress(decodedOwnerEmailAddress))
		               .orElseThrow(() -> new VehicleNotFoundException("No vehicle found for specified user email: " + decodedOwnerEmailAddress));
	}

	@Override
	public void updateCurrentMileage(final String currentMileage, final String ownerEmailAddress) {
		final String decodedOwnerEmailAddress = requestsDecoder.decodeStringParam(ownerEmailAddress);
		final Long decodedCurrentMileage = requestsDecoder.decodeStringParamAndConvertToLong(currentMileage);
		vehicleRepository.updateCurrentMileage(decodedCurrentMileage, decodedOwnerEmailAddress);
	}
}
