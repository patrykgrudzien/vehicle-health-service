package me.grudzien.patryk.integration.registration.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.model.entity.EmailVerificationToken;
import me.grudzien.patryk.utils.appplication.ApplicationZone;
import me.grudzien.patryk.registration.repository.CustomUserRepository;
import me.grudzien.patryk.registration.repository.EmailVerificationTokenRepository;

import static org.assertj.core.api.Assertions.assertThat;

import static me.grudzien.patryk.TestsUtils.TEST_PASSWORD;

@DataJpaTest
class EmailVerificationTokenRepositoryIT {

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    private CustomUserRepository customUserRepository;

    private EmailVerificationToken givenEmailVerificationToken;
    private EmailVerificationToken savedToken;

    private static final ZonedDateTime NOW_PLUS_15_MINUTES = ApplicationZone.POLAND.now().plusMinutes(15L);

    @BeforeEach
    void setUp() {
        givenEmailVerificationToken = prepareTestEmailVerificationToken();
        savedToken = emailVerificationTokenRepository.save(givenEmailVerificationToken);
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
                () -> Assertions.assertEquals(NOW_PLUS_15_MINUTES.getOffset(), foundToken.getExpiryDate().getOffset()),
                () -> Assertions.assertEquals(NOW_PLUS_15_MINUTES.getHour(), foundToken.getExpiryDate().getHour()),
                () -> Assertions.assertEquals(NOW_PLUS_15_MINUTES.getMinute(), foundToken.getExpiryDate().getMinute()),
                () -> Assertions.assertEquals(NOW_PLUS_15_MINUTES.getSecond(), foundToken.getExpiryDate().getSecond()),
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
                () -> Assertions.assertEquals(NOW_PLUS_15_MINUTES.getOffset(), foundToken.getExpiryDate().getOffset()),
                () -> Assertions.assertEquals(NOW_PLUS_15_MINUTES.getHour(), foundToken.getExpiryDate().getHour()),
                () -> Assertions.assertEquals(NOW_PLUS_15_MINUTES.getMinute(), foundToken.getExpiryDate().getMinute()),
                () -> Assertions.assertEquals(NOW_PLUS_15_MINUTES.getSecond(), foundToken.getExpiryDate().getSecond()),
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
                                                           .password(TEST_PASSWORD)
                                                           .isEnabled(false)
                                                           .createdDate(ApplicationZone.POLAND.now())
                                                           .build())
                                     .expiryDate(NOW_PLUS_15_MINUTES)
                                     .build();
    }
}