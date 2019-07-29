package me.grudzien.patryk.configuration.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import me.grudzien.patryk.configuration.properties.cors.CustomCorsProperties;
import me.grudzien.patryk.configuration.properties.heroku.CustomHerokuProperties;
import me.grudzien.patryk.configuration.properties.jwt.CustomJwtProperties;
import me.grudzien.patryk.configuration.properties.oauth2.CustomOAuth2Properties;
import me.grudzien.patryk.configuration.properties.ui.CustomUIMessageCodesProperties;

@Configuration
@EnableConfigurationProperties({
        CustomCorsProperties.class,
        CustomHerokuProperties.class,
        CustomJwtProperties.class,
        CustomOAuth2Properties.class,
        CustomUIMessageCodesProperties.class
})
public class CustomConfigurationProperties {}
