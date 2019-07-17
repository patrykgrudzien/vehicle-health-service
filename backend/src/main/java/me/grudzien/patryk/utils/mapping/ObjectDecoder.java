package me.grudzien.patryk.utils.mapping;

import org.springframework.lang.NonNull;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.authentication.mapping.JwtAuthenticationRequestMapper;
import me.grudzien.patryk.registration.mapping.UserRegistrationDtoMapper;

public interface ObjectDecoder<EncodedObject, Mapper, DecodedObject> {

    DecodedObject apply(@NonNull final EncodedObject encodedObject, @NonNull final Mapper mapper);

    static ObjectDecoder<JwtAuthenticationRequest, JwtAuthenticationRequestMapper, JwtAuthenticationRequest> decodeAuthRequest() {
        return (encodedAuthRequest, jwtAuthenticationRequestMapper) -> jwtAuthenticationRequestMapper.toDecodedAuthRequest(encodedAuthRequest);
    }

    static ObjectDecoder<UserRegistrationDto, UserRegistrationDtoMapper, UserRegistrationDto> decodeUserRegistrationDto() {
        return (encodedDto, userRegistrationDtoMapper) -> userRegistrationDtoMapper.toDecodedUserRegistrationDto(encodedDto);
    }
}
