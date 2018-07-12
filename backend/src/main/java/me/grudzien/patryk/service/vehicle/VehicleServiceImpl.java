package me.grudzien.patryk.service.vehicle;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import java.util.Optional;

import me.grudzien.patryk.domain.dto.vehicle.VehicleDto;
import me.grudzien.patryk.domain.entities.vehicle.Vehicle;
import me.grudzien.patryk.exceptions.vehicle.VehicleNotFoundException;
import me.grudzien.patryk.repository.vehicle.VehicleRepository;
import me.grudzien.patryk.utils.web.RequestsDecoder;

import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;
import static me.grudzien.patryk.utils.log.LogMarkers.METHOD_INVOCATION_MARKER;

@Log4j2
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
	@Cacheable(key = "#root.target.VEHICLE_MILEAGE_CACHE_KEY",
	           unless = "#result == null")
	public Long getVehicleCurrentMileage(final String ownerEmailAddress) {
		final String decodedOwnerEmailAddress = requestsDecoder.decodeStringParam(ownerEmailAddress);

		log.info(METHOD_INVOCATION_MARKER, "(NO CACHE FOUND) => method execution...");

		return Optional.ofNullable(vehicleRepository.getVehicleCurrentMileage(decodedOwnerEmailAddress))
		               .map(mileageFromDB -> {
		               	    VEHICLE_MILEAGE_CACHE_KEY = mileageFromDB.toString();
		               	    log.info(FLOW_MARKER, "({}) cache name has been updated.", VEHICLE_MILEAGE_CACHE_NAME);
		               	    return mileageFromDB;
		               })
		               .orElseThrow(() -> new VehicleNotFoundException("No vehicle found for specified user email: " + decodedOwnerEmailAddress));
	}

	@Override
	@Caching(
		evict = {@CacheEvict(allEntries = true)},
		put = {@CachePut(key = "#root.target.VEHICLE_MILEAGE_CACHE_KEY",
		                 condition = "#newMileage != null && #newMileage.equals(\"\") && !#newMileage.equals(#root.target.VEHICLE_MILEAGE_CACHE_KEY)")}
	)
	public void updateCurrentMileage(final String newMileage, final String ownerEmailAddress) {
		final String decodedOwnerEmailAddress = requestsDecoder.decodeStringParam(ownerEmailAddress);

		log.info(METHOD_INVOCATION_MARKER, "(NO CACHE FOUND) => method execution...");
		VEHICLE_MILEAGE_CACHE_KEY = newMileage;
		log.info(FLOW_MARKER, "({}) cache name has been updated.", VEHICLE_MILEAGE_CACHE_NAME);

		vehicleRepository.updateCurrentMileage(Long.valueOf(newMileage), decodedOwnerEmailAddress);
	}
}
