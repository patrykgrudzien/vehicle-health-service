package me.grudzien.patryk.vehicle.resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import me.grudzien.patryk.vehicle.model.dto.VehicleDto;

import static me.grudzien.patryk.vehicle.resource.VehicleResourceDefinitions.CURRENT_MILEAGE;
import static me.grudzien.patryk.vehicle.resource.VehicleResourceDefinitions.OWNER_EMAIL_ADDRESS;
import static me.grudzien.patryk.vehicle.resource.VehicleResourceDefinitions.UPDATE_CURRENT_MILEAGE;
import static me.grudzien.patryk.vehicle.resource.VehicleResourceDefinitions.VEHICLE_RESOURCE_ROOT;

/**
 *
 */
@RequestMapping(VEHICLE_RESOURCE_ROOT)
public interface VehicleResource {

    /**
     *
     * @param ownerEmailAddress
     * @param webRequest
     * @return
     */
    @GetMapping(OWNER_EMAIL_ADDRESS)
    VehicleDto getVehicleForOwnerEmailAddress(@PathVariable("ownerEmailAddress") String ownerEmailAddress, WebRequest webRequest);

    /**
     *
     * @param ownerEmailAddress
     * @param webRequest
     * @return
     */
    @GetMapping(CURRENT_MILEAGE)
    @PreAuthorize("@requestParamPathVariableGuard.isUserEmailAuthenticated(#ownerEmailAddress)")
    Long getVehicleCurrentMileage(@PathVariable("ownerEmailAddress") String ownerEmailAddress, WebRequest webRequest);

    /**
     *
     * @param vehicleDto
     * @param ownerEmailAddress
     * @param webRequest
     */
    @PutMapping(UPDATE_CURRENT_MILEAGE)
    void updateVehicleCurrentMileage(@RequestBody VehicleDto vehicleDto, @PathVariable("ownerEmailAddress") String ownerEmailAddress,
                                     WebRequest webRequest);
}
