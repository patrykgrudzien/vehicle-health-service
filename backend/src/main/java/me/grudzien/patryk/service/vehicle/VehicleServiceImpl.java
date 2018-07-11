package me.grudzien.patryk.service.vehicle;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
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

	public static String CACHE_KEY = "";

	private final VehicleRepository vehicleRepository;
	private final RequestsDecoder requestsDecoder;
	private final CacheManager cacheManager;

	@Autowired
	public VehicleServiceImpl(final VehicleRepository vehicleRepository, final RequestsDecoder requestsDecoder,
	                          final CacheManager cacheManager) {

		Preconditions.checkNotNull(vehicleRepository, "vehicleRepository cannot be null!");
		Preconditions.checkNotNull(requestsDecoder, "requestsDecoder cannot be null!");

		this.vehicleRepository = vehicleRepository;
		this.requestsDecoder = requestsDecoder;
		this.cacheManager = cacheManager;
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
//	@Cacheable(value = "vehicle-current-mileage", key = "#root.target.CACHE_KEY", unless = "#result == null")
	public VehicleDto findDtoByOwnerEmailAddress(final String ownerEmailAddress) {
		final String decodedOwnerEmailAddress = requestsDecoder.decodeStringParam(ownerEmailAddress);

//		log.info(METHOD_INVOCATION_MARKER, "(CacheManager) => {}", cacheManager);
//		log.info(METHOD_INVOCATION_MARKER, "(CacheManager) => {}", cacheManager);
//		final Long currentMileage = vehicleService.findDtoByOwnerEmailAddress(ownerEmailAddress).getMileage();
//		CACHE_KEY = currentMileage.toString();

		return Optional.ofNullable(vehicleRepository.findDtoByOwnerEmailAddress(decodedOwnerEmailAddress))
		               .orElseThrow(() -> new VehicleNotFoundException("No vehicle found for specified user email: " + decodedOwnerEmailAddress));
	}

	@Override
	public Long getVehicleCurrentMileage(final String ownerEmailAddress) {
		final String decodedOwnerEmailAddress = requestsDecoder.decodeStringParam(ownerEmailAddress);
		return Optional.ofNullable(vehicleRepository.getVehicleCurrentMileage(decodedOwnerEmailAddress))
		               .orElseThrow(() -> new VehicleNotFoundException("No vehicle found for specified user email: " + decodedOwnerEmailAddress));
	}

	@Override
//	@Cacheable(value = "vehicle-updated-mileage", key = "#root.target.CACHE_KEY", condition = "#vehicleDto.mileage != null")
	public void updateCurrentMileage(final String currentMileage, final String ownerEmailAddress) {
		final String decodedOwnerEmailAddress = requestsDecoder.decodeStringParam(ownerEmailAddress);
		final Long decodedCurrentMileage = requestsDecoder.decodeStringParamAndConvertToLong(currentMileage);

//		log.info(METHOD_INVOCATION_MARKER, "(CacheManager) => {}", cacheManager);
//		CACHE_KEY = getVehicleCurrentMileage(ownerEmailAddress, webRequest).toString();

		vehicleRepository.updateCurrentMileage(decodedCurrentMileage, decodedOwnerEmailAddress);
	}
}
