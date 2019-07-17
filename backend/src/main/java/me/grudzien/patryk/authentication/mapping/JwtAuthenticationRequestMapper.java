package me.grudzien.patryk.authentication.mapping;

import org.springframework.beans.factory.annotation.Autowired;

import org.mapstruct.Mapper;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.utils.web.RequestsDecoder;

@Mapper(componentModel = "spring")
public abstract class JwtAuthenticationRequestMapper {

    @Autowired
    private RequestsDecoder requestsDecoder;

    public JwtAuthenticationRequest toDecodedAuthRequest(final JwtAuthenticationRequest encodedAuthRequest) {
        return JwtAuthenticationRequest.Builder()
                                       .email(requestsDecoder.decodeStringParam(encodedAuthRequest.getEmail()))
                                       .password(requestsDecoder.decodeStringParam(encodedAuthRequest.getPassword()))
                                       .build();
    }
}
