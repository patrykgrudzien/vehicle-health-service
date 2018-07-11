package me.grudzien.patryk.controller.vehicle;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.google.common.base.Preconditions;

import me.grudzien.patryk.domain.dto.vehicle.VehicleDto;
import me.grudzien.patryk.service.vehicle.VehicleService;

import static me.grudzien.patryk.utils.log.LogMarkers.METHOD_INVOCATION_MARKER;

@Log4j2
@RestController
@RequestMapping("${custom.properties.endpoints.vehicle-resource.root}")
public class VehicleController {

	private final VehicleService vehicleService;

	@Autowired
	public VehicleController(final VehicleService vehicleService) {
		Preconditions.checkNotNull(vehicleService, "vehicleService cannot be null!");
		this.vehicleService = vehicleService;
	}

	@GetMapping("/vehicle/{ownerEmailAddress}")
	public VehicleDto getVehicleDtoForOwnerEmailAddress(@PathVariable("ownerEmailAddress") final String ownerEmailAddress,
	                                                    @SuppressWarnings("unused") final WebRequest webRequest) {
		log.info(METHOD_INVOCATION_MARKER, "(NO CACHE FOUND) => method execution...");
		return vehicleService.findDtoByOwnerEmailAddress(ownerEmailAddress);
	}

	@GetMapping("/vehicle/get-current-mileage/{ownerEmailAddress}")
	public Long getVehicleCurrentMileage(@PathVariable("ownerEmailAddress") final String ownerEmailAddress,
	                                     @SuppressWarnings("unused") final WebRequest webRequest) {
		log.info(METHOD_INVOCATION_MARKER, "(NO CACHE FOUND) => method execution...");
		return vehicleService.findDtoByOwnerEmailAddress(ownerEmailAddress)
		                     .getMileage();
	}

	@PutMapping("/vehicle/update-current-mileage/{ownerEmailAddress}")
	public void updateVehicleCurrentMileage(@RequestBody final VehicleDto vehicleDto,
	                                        @PathVariable("ownerEmailAddress") final String ownerEmailAddress,
	                                        @SuppressWarnings("unused") final WebRequest webRequest) {
		log.info(METHOD_INVOCATION_MARKER, "(NO CACHE FOUND) => method execution...");
		vehicleService.updateCurrentMileage(vehicleDto.getEncodedMileage(), ownerEmailAddress);
	}
}