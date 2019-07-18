package me.grudzien.patryk.configuration.properties.oauth2;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static me.grudzien.patryk.configuration.properties.PrefixesDefinitions.CUSTOM_OAUTH2_PROPERTIES_PREFIX;

@Getter
@Setter
@ConfigurationProperties(prefix = CUSTOM_OAUTH2_PROPERTIES_PREFIX)
public class CustomOAuth2Properties {

    private Long shortLivedTokenExpiration;
}
