package me.grudzien.patryk.controller.vehicle;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import me.grudzien.patryk.util.web.RequestsDecoder;

@Log4j2
@RestController
@RequestMapping("${custom.properties.endpoints.vehicle-resource.root}")
public class VehicleController {

	private final VehicleService vehicleService;
	private final RequestsDecoder requestsDecoder;

	@Autowired
	public VehicleController(final VehicleService vehicleService, final RequestsDecoder requestsDecoder) {
		Preconditions.checkNotNull(vehicleService, "vehicleService cannot be null!");
		Preconditions.checkNotNull(requestsDecoder, "requestsDecoder cannot be null!");
		this.vehicleService = vehicleService;
		this.requestsDecoder = requestsDecoder;
	}

	@GetMapping("${custom.properties.endpoints.vehicle-resource.get-vehicle}")
	public VehicleDto getVehicleDtoForOwnerEmailAddress(@PathVariable("ownerEmailAddress") final String ownerEmailAddress,
	                                                    @SuppressWarnings("unused") final WebRequest webRequest) {
		return vehicleService.findDtoByOwnerEmailAddress(ownerEmailAddress);
	}

	@GetMapping("${custom.properties.endpoints.vehicle-resource.get-current-mileage}")
	@PreAuthorize("@requestParamPathVariableGuard.isUserEmailAuthenticated(#ownerEmailAddress)")
	public Long getVehicleCurrentMileage(@PathVariable("ownerEmailAddress") final String ownerEmailAddress,
	                                     @SuppressWarnings("unused") final WebRequest webRequest) {
		return vehicleService.getVehicleCurrentMileage(ownerEmailAddress);
	}

	@PutMapping("${custom.properties.endpoints.vehicle-resource.update-current-mileage}")
	public void updateVehicleCurrentMileage(@RequestBody final VehicleDto vehicleDto,
	                                        @PathVariable("ownerEmailAddress") final String ownerEmailAddress,
	                                        @SuppressWarnings("unused") final WebRequest webRequest) {
		/**
		 * Need to decode "newMileage" here (not in service as it was previously) - cache purpose.
		 * Look at -> {@link me.grudzien.patryk.service.vehicle.impl.VehicleServiceImpl#updateCurrentMileage(String, String)} and
		 * "condition" attribute inside {@link org.springframework.cache.annotation.CachePut}
		 */
		final String decodedNewMileage = requestsDecoder.decodeStringParam(vehicleDto.getEncodedMileage());
		vehicleService.updateCurrentMileage(decodedNewMileage, ownerEmailAddress);
	}
}