package me.grudzien.patryk.unit.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.mapper.UserRegistrationDtoMapperImpl;
import me.grudzien.patryk.util.ObjectDecoder;
import me.grudzien.patryk.util.web.RequestsDecoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static me.grudzien.patryk.TestsUtils.ENABLE_ENCODING;
import static me.grudzien.patryk.TestsUtils.TEST_EMAIL;
import static me.grudzien.patryk.TestsUtils.TEST_PASSWORD;
import static me.grudzien.patryk.TestsUtils.prepareUserRegistrationDto;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserRegistrationDtoMapperImpl.class, RequestsDecoder.class})
class UserRegistrationDtoMapperTest {

    @Autowired
    private UserRegistrationDtoMapperImpl userRegistrationDtoMapper;

    @Test
    void toDecodedUserRegistrationDto() {
        // given
        final UserRegistrationDto encodedRegistrationDto = prepareUserRegistrationDto("John", "Snow", TEST_EMAIL, TEST_PASSWORD, ENABLE_ENCODING);

        // when
        final UserRegistrationDto decodedRegistrationDto = ObjectDecoder.decodeUserRegistrationDto().apply(encodedRegistrationDto, userRegistrationDtoMapper);

        // then
        Assertions.assertAll(
                () -> assertThat(decodedRegistrationDto.getFirstName()).isEqualTo("John"),
                () -> assertThat(decodedRegistrationDto.getLastName()).isEqualTo("Snow"),
                () -> assertThat(decodedRegistrationDto.getEmail()).isEqualTo(TEST_EMAIL),
                () -> assertThat(decodedRegistrationDto.getConfirmedEmail()).isEqualTo(TEST_EMAIL),
                () -> assertThat(decodedRegistrationDto.getPassword()).isEqualTo(TEST_PASSWORD),
                () -> assertThat(decodedRegistrationDto.getConfirmedPassword()).isEqualTo(TEST_PASSWORD),
                () -> assertThat(decodedRegistrationDto.getProfilePictureUrl()).isNullOrEmpty(),
                () -> assertThat(decodedRegistrationDto.getRegistrationProvider()).isNull(),
                () -> assertFalse(decodedRegistrationDto.isHasFakeEmail())
        );
    }
}