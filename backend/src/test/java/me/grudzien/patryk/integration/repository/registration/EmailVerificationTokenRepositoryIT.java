package me.grudzien.patryk.integration.repository.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import me.grudzien.patryk.domain.entity.registration.CustomUser;
import me.grudzien.patryk.domain.entity.registration.EmailVerificationToken;
import me.grudzien.patryk.repository.registration.CustomUserRepository;
import me.grudzien.patryk.repository.registration.EmailVerificationTokenRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmailVerificationTokenRepositoryIT {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    private CustomUserRepository customUserRepository;

    private EmailVerificationToken givenEmailVerificationToken;
    private EmailVerificationToken savedToken;

    @BeforeEach
    void setUp() {
        givenEmailVerificationToken = prepareTestEmailVerificationToken();
        savedToken = emailVerificationTokenRepository.save(givenEmailVerificationToken);
        testEntityManager.flush();
    }

    @AfterEach
    void tearDown() {
        emailVerificationTokenRepository.deleteAll();
        customUserRepository.deleteAll();
    }

    @SuppressWarnings("Duplicates")
    @Test
    void findByToken() {
        // when
        final EmailVerificationToken foundToken = emailVerificationTokenRepository.findByToken(givenEmailVerificationToken.getToken());

        // then
        Assertions.assertAll(
                () -> assertThat(foundToken.getId()).isEqualTo(savedToken.getId()),
                () -> assertThat(foundToken.getToken()).isEqualTo(savedToken.getToken()),
                () -> Assertions.assertNotNull(foundToken.getCustomUser()),
                () -> assertThat(foundToken.getCustomUser()).isEqualTo(savedToken.getCustomUser()),
                () -> assertThat(foundToken.getExpiryDate()).isEqualTo(savedToken.getExpiryDate()),
                () -> Assertions.assertEquals(new Date(12345L), foundToken.getExpiryDate()),
                () -> assertThat(customUserRepository.findById(foundToken.getCustomUser().getId())).isNotNull()
        );
    }

    @SuppressWarnings("Duplicates")
    @Test
    void findByCustomUser() {
        // when
        final EmailVerificationToken foundToken = emailVerificationTokenRepository.findByCustomUser(givenEmailVerificationToken.getCustomUser());

        // then
        Assertions.assertAll(
                () -> assertThat(foundToken.getId()).isEqualTo(savedToken.getId()),
                () -> assertThat(foundToken.getToken()).isEqualTo(savedToken.getToken()),
                () -> Assertions.assertNotNull(foundToken.getCustomUser()),
                () -> assertThat(foundToken.getCustomUser()).isEqualTo(savedToken.getCustomUser()),
                () -> assertThat(foundToken.getExpiryDate()).isEqualTo(savedToken.getExpiryDate()),
                () -> Assertions.assertEquals(new Date(12345L), foundToken.getExpiryDate()),
                () -> assertThat(customUserRepository.findById(foundToken.getCustomUser().getId())).isNotNull()
        );
    }

    @Test
    @DisplayName("Testing cascading on token deletion (if user is still present).")
    void deleteToken_userStillPresent() {
        // when
        emailVerificationTokenRepository.deleteById(givenEmailVerificationToken.getId());

        // then
        Assertions.assertAll(
                () -> assertThat(emailVerificationTokenRepository.findById(givenEmailVerificationToken.getId())).isEqualTo(Optional.empty()),
                () -> assertThat(customUserRepository.findById(givenEmailVerificationToken.getCustomUser().getId())).isNotNull()
        );
    }

    private EmailVerificationToken prepareTestEmailVerificationToken() {
        return EmailVerificationToken.Builder()
                                     .token(RandomStringUtils.randomAlphanumeric(25))
                                     .customUser(CustomUser.Builder()
                                                           .firstName("admin")
                                                           .lastName("root")
                                                           .email("test@email.com")
                                                           .password("password")
                                                           .isEnabled(false)
                                                           .createdDate(new Date())
                                                           .build())
                                     .expiryDate(new Date(12345L))
                                     .build();
    }
}