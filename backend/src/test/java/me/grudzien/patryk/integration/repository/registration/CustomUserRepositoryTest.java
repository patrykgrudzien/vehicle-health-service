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

@DataJpaTest
class CustomUserRepositoryTest {

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
                                              .createdDate(new Date())
                                              .hasFakeEmail(true)
                                              .registrationProvider(RegistrationProvider.CUSTOM)
                                              .roles(Sets.newHashSet(new Role(RoleName.ROLE_USER)))
                                              .isEnabled(false)
                                              .build();
        testEntityManager.persist(testUser);
        testEntityManager.flush();

        // when
        final CustomUser foundUser = customUserRepository.findByEmail(testUser.getEmail());

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals("FirstName", foundUser.getFirstName()),
                () -> Assertions.assertEquals("LastName", foundUser.getLastName()),
                () -> Assertions.assertEquals("test@email.com", foundUser.getEmail()),
                () -> Assertions.assertEquals("password", foundUser.getPassword())
        );
    }
}
