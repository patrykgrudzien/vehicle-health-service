package me.grudzien.patryk.vehicle.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Optional;

import me.grudzien.patryk.utils.web.RequestsDecoder;
import me.grudzien.patryk.vehicle.exception.VehicleNotFoundException;
import me.grudzien.patryk.vehicle.model.dto.VehicleDto;
import me.grudzien.patryk.vehicle.model.entity.Vehicle;
import me.grudzien.patryk.vehicle.repository.VehicleRepository;
import me.grudzien.patryk.vehicle.service.VehicleService;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
@Service
@CacheConfig(cacheNames = VehicleServiceImpl.VEHICLE_MILEAGE_CACHE_NAME)
public class VehicleServiceImpl implements VehicleService {

	public static final String VEHICLE_MILEAGE_CACHE_NAME = "vehicle-current-mileage";
	@Getter
	private static String VEHICLE_MILEAGE_CACHE_KEY = "0";

	private final VehicleRepository vehicleRepository;
	private final RequestsDecoder requestsDecoder;

	@Autowired
	public VehicleServiceImpl(final VehicleRepository vehicleRepository, final RequestsDecoder requestsDecoder) {
		checkNotNull(vehicleRepository, "vehicleRepository cannot be null!");
		checkNotNull(requestsDecoder, "requestsDecoder cannot be null!");

		this.vehicleRepository = vehicleRepository;
		this.requestsDecoder = requestsDecoder;
	}

	@Override
	public Vehicle findByCustomUserId(final String customUserId) {
		final Long decodedCustomUserId = requestsDecoder.decodeStringParamAndConvertToLong(customUserId);
		return Optional.ofNullable(vehicleRepository.findByCustomUser_Id(decodedCustomUserId))
		               .orElseThrow(() -> new VehicleNotFoundException("No vehicle found for specified userId: " + decodedCustomUserId));
	}

	@Override
	public Vehicle findByCustomUserEmail(final String customUserEmail) {
		final String decodedCustomUserEmail = requestsDecoder.decodeStringParam(customUserEmail);
		return Optional.ofNullable(vehicleRepository.findByCustomUser_Email(decodedCustomUserEmail))
		               .orElseThrow(() -> new VehicleNotFoundException("No vehicle found for specified user email: " + decodedCustomUserEmail));
	}

	@Override
	public VehicleDto findDtoByOwnerEmailAddress(final String ownerEmailAddress) {
		final String decodedOwnerEmailAddress = requestsDecoder.decodeStringParam(ownerEmailAddress);
		return Optional.ofNullable(vehicleRepository.findDtoByOwnerEmailAddress(decodedOwnerEmailAddress))
		               .orElseThrow(() -> new VehicleNotFoundException("No vehicle found for specified user email: " + decodedOwnerEmailAddress));
	}

	@Override
	@Cacheable(key = "#root.target.VEHICLE_MILEAGE_CACHE_KEY",
	           unless = "#result == null")
	public Long getVehicleCurrentMileage(final String ownerEmailAddress) {
		final String decodedOwnerEmailAddress = requestsDecoder.decodeStringParam(ownerEmailAddress);

		log.info("(NO CACHE FOUND) => method execution...");

		return Optional.ofNullable(vehicleRepository.getVehicleCurrentMileage(decodedOwnerEmailAddress))
		               .map(mileageFromDB -> {
		               	    VEHICLE_MILEAGE_CACHE_KEY = mileageFromDB.toString();
		               	    log.info("({}) cache name has been updated.", VEHICLE_MILEAGE_CACHE_NAME);
		               	    return mileageFromDB;
		               })
		               .orElseThrow(() -> new VehicleNotFoundException("No vehicle found for specified user email: " + decodedOwnerEmailAddress));
	}

	@Override
	@Caching(
		evict = {@CacheEvict(key = "#root.target.VEHICLE_MILEAGE_CACHE_KEY")},
		put = {@CachePut(key = "#root.target.VEHICLE_MILEAGE_CACHE_KEY",
		                 condition = "#newMileage != null && #newMileage.equals(\"\") && !#newMileage.equals(#root.target.VEHICLE_MILEAGE_CACHE_KEY)")}
	)
	public void updateCurrentMileage(final String newMileage, final String ownerEmailAddress) {
		final String decodedOwnerEmailAddress = requestsDecoder.decodeStringParam(ownerEmailAddress);

		log.info("(NO CACHE FOUND) => method execution...");
		VEHICLE_MILEAGE_CACHE_KEY = newMileage;
		log.info("({}) cache name has been updated.", VEHICLE_MILEAGE_CACHE_NAME);

		vehicleRepository.updateCurrentMileage(Long.valueOf(newMileage), decodedOwnerEmailAddress);
	}
}
