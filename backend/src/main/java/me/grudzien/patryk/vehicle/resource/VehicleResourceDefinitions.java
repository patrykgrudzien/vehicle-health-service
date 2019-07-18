package me.grudzien.patryk.vehicle.resource;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

import static me.grudzien.patryk.utils.appplication.ApiVersion.API_VERSION;

@NoArgsConstructor(access = PRIVATE)
public final class VehicleResourceDefinitions {

    private static final String VEHICLE_RESOURCE = "/vehicle";

    public static final String VEHICLE_RESOURCE_ROOT = API_VERSION + VEHICLE_RESOURCE;
    public static final String OWNER_EMAIL_ADDRESS = "/{ownerEmailAddress}";
    public static final String CURRENT_MILEAGE = "/current-mileage/{ownerEmailAddress}";
    public static final String UPDATE_CURRENT_MILEAGE = "/update-current-mileage/{ownerEmailAddress}";
}
