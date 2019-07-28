package me.grudzien.patryk.configuration.properties.jwt;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static me.grudzien.patryk.configuration.properties.PrefixesDefinitions.CUSTOM_JWT_PROPERTIES_PREFIX;

@Getter
@Setter
//@Configuration
@ConfigurationProperties(prefix = CUSTOM_JWT_PROPERTIES_PREFIX)
public class CustomJwtProperties {

    private String tokenSecretKey;
    private Long tokenExpiration;
}
