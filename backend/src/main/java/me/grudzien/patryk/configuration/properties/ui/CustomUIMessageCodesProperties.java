package me.grudzien.patryk.configuration.properties.ui;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static me.grudzien.patryk.configuration.properties.PrefixesDefinitions.CUSTOM_UI_MESSAGE_CODES_PROPERTIES_PREFIX;

@Getter
@Setter
@ConfigurationProperties(prefix = CUSTOM_UI_MESSAGE_CODES_PROPERTIES_PREFIX)
public class CustomUIMessageCodesProperties {

    private String loginFormValidationErrors;
}
