package me.grudzien.patryk.utils.web;

import org.springframework.lang.NonNull;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.authentication.mapping.JwtAuthenticationRequestMapper;
import me.grudzien.patryk.registration.mapping.UserRegistrationDtoMapper;

public interface ObjectDecoder<Object, Mapper> {

    Object apply(@NonNull final Object encodedObject, @NonNull final Mapper mapper);

    static ObjectDecoder<JwtAuthenticationRequest, JwtAuthenticationRequestMapper> authRequestDecoder() {
        return (encodedAuthRequest, jwtAuthenticationRequestMapper) -> jwtAuthenticationRequestMapper.toDecodedAuthRequest(encodedAuthRequest);
    }

    static ObjectDecoder<UserRegistrationDto, UserRegistrationDtoMapper> userRegistrationDtoDecoder() {
        return (encodedDto, userRegistrationDtoMapper) -> userRegistrationDtoMapper.toDecodedUserRegistrationDto(encodedDto);
    }
}
