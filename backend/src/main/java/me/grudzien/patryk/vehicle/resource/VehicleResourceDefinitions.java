package me.grudzien.patryk.vehicle.resource;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

import static me.grudzien.patryk.utils.app.ApiVersion.API_VERSION;

@NoArgsConstructor(access = PRIVATE)
final class VehicleResourceDefinitions {

    private static final String VEHICLE_RESOURCE = "/vehicle";

    static final String VEHICLE_RESOURCE_ROOT = API_VERSION + VEHICLE_RESOURCE;
    static final String OWNER_EMAIL_ADDRESS = "/{ownerEmailAddress}";
    static final String CURRENT_MILEAGE = "/current-mileage/{ownerEmailAddress}";
    static final String UPDATE_CURRENT_MILEAGE = "/update-current-mileage/{ownerEmailAddress}";
}
