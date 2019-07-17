package me.grudzien.patryk.utils;

import lombok.extern.log4j.Log4j2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.model.entity.Privilege;
import me.grudzien.patryk.registration.model.entity.Role;
import me.grudzien.patryk.registration.model.enums.PrivilegeName;
import me.grudzien.patryk.registration.model.enums.RegistrationProvider;
import me.grudzien.patryk.registration.model.enums.RoleName;
import me.grudzien.patryk.registration.repository.CustomUserRepository;
import me.grudzien.patryk.utils.app.ApplicationZone;
import me.grudzien.patryk.utils.app.SpringAppProfiles;
import me.grudzien.patryk.vehicle.model.entity.Engine;
import me.grudzien.patryk.vehicle.model.entity.Vehicle;
import me.grudzien.patryk.vehicle.model.enums.EngineType;
import me.grudzien.patryk.vehicle.model.enums.VehicleType;

import static com.google.common.base.Preconditions.checkNotNull;

@Log4j2
@Component
public class TestUserInitializer implements CommandLineRunner {

	private final CustomUserRepository customUserRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final Environment environment;

	public TestUserInitializer(final CustomUserRepository customUserRepository,
	                           final BCryptPasswordEncoder passwordEncoder,
	                           final Environment environment) {

		checkNotNull(customUserRepository, "customUserRepository cannot be null!");
		checkNotNull(passwordEncoder, "passwordEncoder cannot be null!");
		checkNotNull(environment, "environment cannot be null!");

		this.customUserRepository = customUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.environment = environment;
		log.info("{} configured to insert test user.", this.getClass().getSimpleName());
	}

	@SuppressWarnings("Duplicates")
	@Override
	public void run(final String... args) {
		final Optional<String> activeSpringProfile = Arrays.stream(environment.getActiveProfiles()).findFirst();
		log.info("Active Spring profile -> ({})", activeSpringProfile.orElse(null));

		// it's done just to avoid creating new account each time Spring Application is restarted
		if (activeSpringProfile.isPresent() && (activeSpringProfile.get().equals(SpringAppProfiles.DEV_HOME.getYmlName()) ||
		                                        activeSpringProfile.get().equals(SpringAppProfiles.H2_IN_MEMORY.getYmlName()))) {

			log.info("Creating test user...");
			final CustomUser testUser = CustomUser.Builder()
			                                      .firstName("Admin")
			                                      .lastName("Root")
			                                      .email("admin.root@gmail.com")
			                                      .hasFakeEmail(true)
			                                      .password(passwordEncoder.encode("admin"))
                                                  .profilePictureUrl("www.my-profile-photo.fakeUrl.com")
                                                  .registrationProvider(RegistrationProvider.CUSTOM)
			                                      .roles(Collections.singleton(Role.Builder()
			                                                                       .roleName(RoleName.ROLE_ADMIN)
			                                                                       .privileges(Sets.newHashSet(Privilege.Builder()
			                                                                                                            .privilegeName(PrivilegeName.CAN_DO_EVERYTHING)
			                                                                                                            .build()))
			                                                                       .build()))
			                                      .isEnabled(Boolean.TRUE)
			                                      .createdDate(ApplicationZone.POLAND.now())
			                                      .build();
			log.info("Test user ({}) created.", testUser.getEmail());

			log.info("Creating engine...");
			final Engine engine = Engine.Builder()
			                            .engineType(EngineType.DIESEL)
			                            .build();

			log.info("Creating vehicle...");
			final Vehicle vehicle = Vehicle.Builder()
			                               .vehicleType(VehicleType.CAR)
			                               .engine(engine)
			                               .build();
			vehicle.setMileage(0L);
			vehicle.setCustomUser(testUser);
			testUser.setVehicles(Lists.newArrayList(vehicle));

			customUserRepository.save(testUser);
			log.info("------------------------------------------------------------------------------------------------");
			log.info("Test user ({}) saved into database.", testUser.getEmail());
			log.info("Engine with ID: ({}) saved into database.", engine.getId());
			log.info("Vehicle with ID: ({}) saved into database.", vehicle.getId());
			log.info("------------------------------------------------------------------------------------------------");

		} else {
			log.info("------------------------------------------------------------------------------------------------");
			log.info("Nothing additional to be run after application startup.");
			log.info("------------------------------------------------------------------------------------------------");
		}
	}
}
