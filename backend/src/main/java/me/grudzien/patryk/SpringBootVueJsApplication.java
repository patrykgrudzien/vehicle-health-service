package me.grudzien.patryk;

import lombok.extern.log4j.Log4j2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.entities.engine.Engine;
import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.domain.entities.registration.Privilege;
import me.grudzien.patryk.domain.entities.registration.Role;
import me.grudzien.patryk.domain.entities.vehicle.Vehicle;
import me.grudzien.patryk.domain.enums.registration.PrivilegeName;
import me.grudzien.patryk.domain.enums.registration.RoleName;
import me.grudzien.patryk.domain.enums.SpringAppProfiles;
import me.grudzien.patryk.domain.enums.engine.EngineType;
import me.grudzien.patryk.domain.enums.vehicle.VehicleType;
import me.grudzien.patryk.repository.registration.CustomUserRepository;

import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;

@Log4j2
@SpringBootApplication
@EnableConfigurationProperties(CustomApplicationProperties.class)
public class SpringBootVueJsApplication implements CommandLineRunner {

	private final CustomUserRepository customUserRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final Environment environment;

	public SpringBootVueJsApplication(final CustomUserRepository customUserRepository,
	                                  final BCryptPasswordEncoder passwordEncoder,
	                                  final Environment environment) {

		Preconditions.checkNotNull(customUserRepository, "customUserRepository cannot be null!");
		Preconditions.checkNotNull(passwordEncoder, "passwordEncoder cannot be null!");
		Preconditions.checkNotNull(environment, "environment cannot be null!");

		this.customUserRepository = customUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.environment = environment;
	}

	public static void main(final String[] args) {
		SpringApplication.run(SpringBootVueJsApplication.class, args);
	}

	@Override
	public void run(final String... args) {

		final Optional<String> activeSpringProfile = Arrays.stream(environment.getActiveProfiles()).findFirst();
		log.info(FLOW_MARKER, "Active Spring profile -> ({})", activeSpringProfile.orElse(null));

		// it's done just to avoid creating new account each time Spring Application is restarted
		if (activeSpringProfile.isPresent() && (activeSpringProfile.get().equals(SpringAppProfiles.DEV_HOME.getYmlName()) ||
		                                        activeSpringProfile.get().equals(SpringAppProfiles.DEV_OFFICE.getYmlName()))) {

			log.info(FLOW_MARKER, "Creating test user...");
			final CustomUser testUser = CustomUser.Builder()
			                                        .firstName("Admin")
			                                        .lastName("Root")
			                                        .email("admin.root@gmail.com")
			                                        .password(passwordEncoder.encode("admin"))
			                                        .roles(Collections.singleton(Role.Builder()
			                                                                         .roleName(RoleName.ROLE_ADMIN)
			                                                                         .privileges(Sets.newHashSet(Privilege.Builder()
			                                                                                                              .privilegeName(PrivilegeName.CAN_DO_EVERYTHING)
			                                                                                                              .build()))
			                                                                         .build()))
			                                        .isEnabled(Boolean.TRUE)
			                                        .createdDate(new Date())
			                                        .build();
			log.info(FLOW_MARKER, "Test user ({}) created.", testUser.getEmail());

			log.info(FLOW_MARKER, "Creating engine...");
			final Engine engine = Engine.Builder()
			                            .engineType(EngineType.DIESEL)
			                            .build();

			log.info(FLOW_MARKER, "Creating vehicle...");
			final Vehicle vehicle = Vehicle.Builder()
			                               .vehicleType(VehicleType.CAR)
			                               .engine(engine)
			                               .build();
			vehicle.setMileage(0L);
			vehicle.setCustomUser(testUser);
			testUser.setVehicles(Lists.newArrayList(vehicle));

			customUserRepository.save(testUser);
			log.info("------------------------------------------------------------------------------------------------");
			log.info(FLOW_MARKER, "Test user ({}) saved into database.", testUser.getEmail());
			log.info(FLOW_MARKER, "Engine with ID: ({}) saved into database.", engine.getId());
			log.info(FLOW_MARKER, "Vehicle with ID: ({}) saved into database.", vehicle.getId());
			log.info("------------------------------------------------------------------------------------------------");

		} else {
			log.info("------------------------------------------------------------------------------------------------");
			log.info(FLOW_MARKER, "Nothing additional to be run after application startup.");
			log.info("------------------------------------------------------------------------------------------------");
		}
	}
}
