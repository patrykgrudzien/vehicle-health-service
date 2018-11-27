package me.grudzien.patryk.integration.repository.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;

import java.util.Date;

import me.grudzien.patryk.domain.entity.registration.CustomUser;
import me.grudzien.patryk.domain.entity.registration.Role;
import me.grudzien.patryk.domain.enums.registration.RegistrationProvider;
import me.grudzien.patryk.domain.enums.registration.RoleName;
import me.grudzien.patryk.repository.registration.CustomUserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomUserRepositoryIT {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CustomUserRepository customUserRepository;

    @Test
    @DisplayName("findByEmail(String email). Plain database password.")
    void testFindByEmail() {
        // given
        final CustomUser testUser = CustomUser.Builder()
                                              .firstName("FirstName")
                                              .lastName("LastName")
                                              .email("test@email.com")
                                              .password("password")
                                              .createdDate(new Date(123456789L))
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
                () -> Assertions.assertEquals(new Date(123456789L), foundUser.getCreatedDate()),
                () -> Assertions.assertTrue(foundUser.isHasFakeEmail()),
                () -> Assertions.assertEquals(RegistrationProvider.CUSTOM, foundUser.getRegistrationProvider()),
                () -> assertThat(foundUser.getRoles()).hasSize(1),
                () -> Assertions.assertFalse(foundUser.isEnabled())
        );
    }
}
