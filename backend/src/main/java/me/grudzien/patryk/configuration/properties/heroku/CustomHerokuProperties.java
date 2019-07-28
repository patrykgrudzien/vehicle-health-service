package me.grudzien.patryk.configuration.properties.heroku;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static me.grudzien.patryk.configuration.properties.PrefixesDefinitions.CUSTOM_HEROKU_PROPERTIES_PREFIX;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = CUSTOM_HEROKU_PROPERTIES_PREFIX)
public class CustomHerokuProperties {

    private String herokuAppHostUrl;
}
