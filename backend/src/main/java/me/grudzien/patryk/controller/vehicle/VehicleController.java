package me.grudzien.patryk.controller.vehicle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.grudzien.patryk.domain.entities.vehicle.Vehicle;
import me.grudzien.patryk.service.vehicle.VehicleService;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

	private final VehicleService vehicleService;

	@Autowired
	public VehicleController(final VehicleService vehicleService) {
		this.vehicleService = vehicleService;
	}

	@GetMapping("/{customUserEmail}")
	public Vehicle getVehicleForCustomUser(@PathVariable("customUserEmail") final String customUserEmail) {
		return vehicleService.findByCustomUserEmail(customUserEmail);
	}
}