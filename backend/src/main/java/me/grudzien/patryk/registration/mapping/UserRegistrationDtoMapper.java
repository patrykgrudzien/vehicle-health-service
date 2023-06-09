package me.grudzien.patryk.registration.mapping;

import org.springframework.beans.factory.annotation.Autowired;

import org.mapstruct.Mapper;

import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.utils.web.RequestsDecoder;

@Mapper(componentModel = "spring")
public abstract class UserRegistrationDtoMapper {

    @Autowired
    private RequestsDecoder requestsDecoder;

    public UserRegistrationDto toDecodedUserRegistrationDto(final UserRegistrationDto encodedDto) {
        return UserRegistrationDto.Builder()
                                  .firstName(requestsDecoder.decodeStringParam(encodedDto.getFirstName()))
                                  .lastName(requestsDecoder.decodeStringParam(encodedDto.getLastName()))
                                  .email(requestsDecoder.decodeStringParam(encodedDto.getEmail()))
                                  .confirmedEmail(requestsDecoder.decodeStringParam(encodedDto.getConfirmedEmail()))
                                  .password(requestsDecoder.decodeStringParam(encodedDto.getPassword()))
                                  .confirmedPassword(requestsDecoder.decodeStringParam(encodedDto.getConfirmedPassword()))
                                  .hasFakeEmail(encodedDto.isHasFakeEmail())
                                  .build();
    }
}
