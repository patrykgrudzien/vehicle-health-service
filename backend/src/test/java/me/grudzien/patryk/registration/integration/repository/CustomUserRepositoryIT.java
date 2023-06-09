package me.grudzien.patryk.registration.integration.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;

import java.time.Duration;
import java.time.Period;
import java.time.ZonedDateTime;

import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.model.entity.Role;
import me.grudzien.patryk.utils.appplication.ApplicationZone;
import me.grudzien.patryk.registration.model.enums.RegistrationProvider;
import me.grudzien.patryk.registration.model.enums.RoleName;
import me.grudzien.patryk.registration.repository.CustomUserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomUserRepositoryIT {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CustomUserRepository customUserRepository;

    private final ZonedDateTime createdDate = ApplicationZone.POLAND.now();

    @Test
    @DisplayName("findByEmail(String email). Plain database password.")
    void testFindByEmail() {
        // given
        final CustomUser testUser = CustomUser.Builder()
                                              .firstName("FirstName")
                                              .lastName("LastName")
                                              .email("test@email.com")
                                              .password("password")
                                              .createdDate(createdDate)
                                              .hasFakeEmail(true)
                                              .registrationProvider(RegistrationProvider.CUSTOM)
                                              .roles(Sets.newHashSet(new Role(RoleName.ROLE_USER)))
                                              .isEnabled(false)
                                              .build();
	    final CustomUser savedUser = testEntityManager.persistFlushFind(testUser);

        // when
        final CustomUser foundUser = customUserRepository.findByEmail(testUser.getEmail());

        // then
        Assertions.assertAll(
                () -> assertThat(foundUser.getFirstName()).isEqualTo(savedUser.getFirstName()),
                () -> assertThat(foundUser.getLastName()).isEqualTo(savedUser.getLastName()),
                () -> assertThat(foundUser.getEmail()).isEqualTo(savedUser.getEmail()),
                () -> assertThat(foundUser.getPassword()).isEqualTo(savedUser.getPassword()),
                () -> Assertions.assertEquals("FirstName", foundUser.getFirstName()),
                () -> Assertions.assertEquals("LastName", foundUser.getLastName()),
                () -> Assertions.assertEquals("test@email.com", foundUser.getEmail()),
                () -> Assertions.assertEquals("password", foundUser.getPassword()),
                () -> Assertions.assertEquals(Period.ZERO, Period.between(testUser.getCreatedDate().toLocalDate(), foundUser.getCreatedDate().toLocalDate())),
                () -> Assertions.assertEquals(Duration.ZERO, Duration.between(testUser.getCreatedDate().toLocalTime(), foundUser.getCreatedDate().toLocalTime())),
                () -> Assertions.assertTrue(foundUser.isHasFakeEmail()),
                () -> Assertions.assertEquals(RegistrationProvider.CUSTOM, foundUser.getRegistrationProvider()),
                () -> assertThat(foundUser.getRoles()).hasSize(1),
                () -> Assertions.assertFalse(foundUser.isEnabled())
        );
    }
}
