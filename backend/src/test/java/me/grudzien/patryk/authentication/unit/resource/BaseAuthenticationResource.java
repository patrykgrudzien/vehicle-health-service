package me.grudzien.patryk.authentication.unit.resource;

import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import me.grudzien.patryk.DefaultResourceConfiguration;
import me.grudzien.patryk.authentication.AbstractAuthenticationResourceHelper;
import me.grudzien.patryk.authentication.mapping.JwtAuthenticationRequestMapper;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.configuration.properties.ui.CustomUIMessageCodesProperties;

import static lombok.AccessLevel.NONE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@Import(DefaultResourceConfiguration.class)
@MockBean(CustomUIMessageCodesProperties.class)
@NoArgsConstructor(access = NONE)
abstract class BaseAuthenticationResource extends AbstractAuthenticationResourceHelper {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    protected JwtAuthenticationRequestMapper authRequestMapper;

    void setupAuthMapperToReturn(final JwtAuthenticationRequest decodedAuthRequest) {
        given(authRequestMapper.toDecodedAuthRequest(any(JwtAuthenticationRequest.class))).willReturn(decodedAuthRequest);
    }

    void verifyAuthMapper() {
        verify(authRequestMapper).toDecodedAuthRequest(any(JwtAuthenticationRequest.class));
    }
}
