package me.grudzien.patryk.integration.repository.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

import me.grudzien.patryk.domain.entity.registration.CustomUser;
import me.grudzien.patryk.domain.entity.registration.EmailVerificationToken;
import me.grudzien.patryk.repository.registration.EmailVerificationTokenRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmailVerificationTokenRepositoryIT {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @SuppressWarnings("Duplicates")
    @Test
    void findByToken() {
        // given
        final EmailVerificationToken emailVerificationToken = prepareTestEmailVerificationToken();
        final EmailVerificationToken savedToken = emailVerificationTokenRepository.save(emailVerificationToken);
        testEntityManager.flush();

        // when
        final EmailVerificationToken foundToken = emailVerificationTokenRepository.findByToken(emailVerificationToken.getToken());

        // then
        Assertions.assertAll(
                () -> assertThat(foundToken.getId()).isEqualTo(savedToken.getId()),
                () -> assertThat(foundToken.getToken()).isEqualTo(savedToken.getToken()),
                () -> Assertions.assertNotNull(foundToken.getCustomUser()),
                () -> assertThat(foundToken.getCustomUser()).isEqualTo(savedToken.getCustomUser()),
                () -> assertThat(foundToken.getExpiryDate()).isEqualTo(savedToken.getExpiryDate()),
                () -> Assertions.assertEquals(new Date(12345L), foundToken.getExpiryDate())
        );
    }

    @SuppressWarnings("Duplicates")
    @Test
    void findByCustomUser() {
        // given
        final EmailVerificationToken emailVerificationToken = prepareTestEmailVerificationToken();
        final EmailVerificationToken savedToken = emailVerificationTokenRepository.save(emailVerificationToken);
        testEntityManager.flush();

        // when
        final EmailVerificationToken foundToken = emailVerificationTokenRepository.findByCustomUser(emailVerificationToken.getCustomUser());

        // then
        Assertions.assertAll(
                () -> assertThat(foundToken.getId()).isEqualTo(savedToken.getId()),
                () -> assertThat(foundToken.getToken()).isEqualTo(savedToken.getToken()),
                () -> Assertions.assertNotNull(foundToken.getCustomUser()),
                () -> assertThat(foundToken.getCustomUser()).isEqualTo(savedToken.getCustomUser()),
                () -> assertThat(foundToken.getExpiryDate()).isEqualTo(savedToken.getExpiryDate()),
                () -> Assertions.assertEquals(new Date(12345L), foundToken.getExpiryDate())
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