package me.grudzien.patryk.unit.authentication.resource;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;

import me.grudzien.patryk.authentication.mapping.JwtAuthenticationRequestMapper;
import me.grudzien.patryk.configuration.properties.ui.CustomUIMessageCodesProperties;
import me.grudzien.patryk.utils.validation.ValidationService;

@MockBeans({
        @MockBean(JwtAuthenticationRequestMapper.class),
        @MockBean(CustomUIMessageCodesProperties.class),
        @MockBean(ValidationService.class),
})
abstract class BaseAuthenticationResource {
}
