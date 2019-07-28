package me.grudzien.patryk.configuration.properties.cors;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static me.grudzien.patryk.configuration.properties.PrefixesDefinitions.CUSTOM_CORS_PROPERTIES_PREFIX;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = CUSTOM_CORS_PROPERTIES_PREFIX)
public class CustomCorsProperties {

    private String frontEndAppLocalHostUrl;
    private String backEndAppLocalHostUrl;
}
