package me.grudzien.patryk.vehicle.resource.impl;

import lombok.extern.log4j.Log4j2;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import me.grudzien.patryk.vehicle.model.dto.VehicleDto;
import me.grudzien.patryk.vehicle.resource.VehicleResource;
import me.grudzien.patryk.vehicle.service.VehicleService;
import me.grudzien.patryk.utils.web.RequestsDecoder;
import me.grudzien.patryk.vehicle.service.impl.VehicleServiceImpl;

import static com.google.common.base.Preconditions.checkNotNull;

@Log4j2
@RestController
public class VehicleResourceImpl implements VehicleResource {

    private final VehicleService vehicleService;
    private final RequestsDecoder requestsDecoder;

    public VehicleResourceImpl(final VehicleService vehicleService, final RequestsDecoder requestsDecoder) {
        checkNotNull(vehicleService, "vehicleService cannot be null!");
        checkNotNull(requestsDecoder, "requestsDecoder cannot be null!");

        this.vehicleService = vehicleService;
        this.requestsDecoder = requestsDecoder;
    }

    @Override
    public VehicleDto getVehicleForOwnerEmailAddress(final String ownerEmailAddress, final WebRequest webRequest) {
        return vehicleService.findDtoByOwnerEmailAddress(ownerEmailAddress);
    }

    @Override
    public Long getVehicleCurrentMileage(final String ownerEmailAddress, final WebRequest webRequest) {
        return vehicleService.getVehicleCurrentMileage(ownerEmailAddress);
    }

    @Override
    public void updateVehicleCurrentMileage(final VehicleDto vehicleDto, final String ownerEmailAddress, final WebRequest webRequest) {
        /**
		 * Need to decode "newMileage" here (not in service as it was previously) - cache purpose.
		 * Look at -> {@link VehicleServiceImpl#updateCurrentMileage(String, String)} and
		 * "condition" attribute inside {@link org.springframework.cache.annotation.CachePut}
		 */
		final String decodedNewMileage = requestsDecoder.decodeStringParam(vehicleDto.getEncodedMileage());
		vehicleService.updateCurrentMileage(decodedNewMileage, ownerEmailAddress);
    }
}
