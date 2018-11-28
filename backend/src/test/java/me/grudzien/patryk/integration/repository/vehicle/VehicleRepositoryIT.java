package me.grudzien.patryk.integration.repository.vehicle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import me.grudzien.patryk.domain.dto.vehicle.VehicleDto;
import me.grudzien.patryk.domain.entity.engine.Engine;
import me.grudzien.patryk.domain.entity.registration.CustomUser;
import me.grudzien.patryk.domain.entity.vehicle.Vehicle;
import me.grudzien.patryk.domain.enums.engine.EngineType;
import me.grudzien.patryk.domain.enums.vehicle.VehicleType;
import me.grudzien.patryk.repository.vehicle.VehicleRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VehicleRepositoryIT {

	@Autowired
	private TestEntityManager testEntityManager;

	@Autowired
	private VehicleRepository vehicleRepository;

	private static final String OWNER_EMAIL_ADDRESS = "test@email.com";
	private static final Long VEHICLE_MILEAGE = 12345L;

	private Vehicle savedVehicle;

	@BeforeEach
	void setUp() {
		savedVehicle = vehicleRepository.save(prepareTestVehicle());
		testEntityManager.flush();
	}

	@AfterEach
	void tearDown() {
		vehicleRepository.deleteAll();
	}

	@Test
	void testFindDtoByOwnerEmailAddress() {
		// when
		final VehicleDto foundDto = vehicleRepository.findDtoByOwnerEmailAddress(OWNER_EMAIL_ADDRESS);

		// then
		Assertions.assertAll(
				() -> assertThat(foundDto.getVehicleType()).isEqualTo(savedVehicle.getVehicleType()),
				() -> assertThat(foundDto.getEngineType()).isEqualTo(savedVehicle.getEngine().getEngineType()),
				() -> assertThat(foundDto.getMileage()).isEqualTo(savedVehicle.getMileage()),
				() -> assertThat(foundDto.getOwnerId()).isEqualTo(savedVehicle.getCustomUser().getId()),
				() -> assertThat(foundDto.getOwnerEmailAddress()).isEqualTo(savedVehicle.getCustomUser().getEmail())
		);
	}

	@Test
	void testGetVehicleCurrentMileage() {
		// when
		final Long vehicleCurrentMileage = vehicleRepository.getVehicleCurrentMileage(OWNER_EMAIL_ADDRESS);

		// then
		assertThat(vehicleCurrentMileage).isEqualTo(VEHICLE_MILEAGE);
	}

	@Test
	void testUpdateCurrentMileage() {
		// given
		final Long newMileage = 99999L;

		// when
		vehicleRepository.updateCurrentMileage(newMileage, OWNER_EMAIL_ADDRESS);

		// then
		final Vehicle updatedVehicle = testEntityManager.persistFlushFind(vehicleRepository.findByCustomUser_Email(savedVehicle.getCustomUser().getEmail()));
		assertThat(updatedVehicle.getMileage()).isEqualTo(newMileage);
	}

	private Vehicle prepareTestVehicle() {
		return Vehicle.Builder()
		              .mileage(VEHICLE_MILEAGE)
		              .customUser(CustomUser.Builder()
		                                    .firstName("Name")
		                                    .lastName("LastName")
		                                    .email(OWNER_EMAIL_ADDRESS)
		                                    .password("password")
		                                    .isEnabled(false)
		                                    .createdDate(new Date())
		                                    .build())
		              .vehicleType(VehicleType.CAR)
		              .engine(Engine.Builder()
		                            .engineType(EngineType.HYBRID)
		                            .build())
		              .build();
	}
}
