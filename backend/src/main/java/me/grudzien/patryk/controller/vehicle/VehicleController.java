package me.grudzien.patryk.controller.vehicle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.grudzien.patryk.domain.dto.vehicle.VehicleDto;
import me.grudzien.patryk.service.vehicle.VehicleService;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

	private final VehicleService vehicleService;

	@Autowired
	public VehicleController(final VehicleService vehicleService) {
		this.vehicleService = vehicleService;
	}

	@GetMapping("/{ownerEmailAddress}")
	public VehicleDto getVehicleDtoForOwnerEmailAddress(@PathVariable("ownerEmailAddress") final String ownerEmailAddress) {
		return vehicleService.findDtoByOwnerEmailAddress(ownerEmailAddress);
	}

	@GetMapping("/get-current-mileage/{ownerEmailAddress}")
	public Long getVehicleCurrentMileage(@PathVariable("ownerEmailAddress") final String ownerEmailAddress) {
		return vehicleService.findDtoByOwnerEmailAddress(ownerEmailAddress).getMileage();
	}
}