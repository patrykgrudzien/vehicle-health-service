package me.grudzien.patryk.util;

import org.springframework.lang.NonNull;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.mapper.JwtAuthenticationRequestMapper;
import me.grudzien.patryk.mapper.UserRegistrationDtoMapper;

public interface ObjectDecoder<EncodedObject, Mapper, DecodedObject> {

    DecodedObject apply(@NonNull final EncodedObject encodedObject, @NonNull final Mapper mapper);

    static ObjectDecoder<JwtAuthenticationRequest, JwtAuthenticationRequestMapper, JwtAuthenticationRequest> decodeAuthRequest() {
        return (encodedAuthRequest, jwtAuthenticationRequestMapper) -> jwtAuthenticationRequestMapper.toDecodedAuthRequest(encodedAuthRequest);
    }

    static ObjectDecoder<UserRegistrationDto, UserRegistrationDtoMapper, UserRegistrationDto> decodeUserRegistrationDto() {
        return (encodedDto, userRegistrationDtoMapper) -> userRegistrationDtoMapper.toDecodedUserRegistrationDto(encodedDto);
    }
}
