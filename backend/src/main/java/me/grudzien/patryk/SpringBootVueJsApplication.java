package me.grudzien.patryk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CustomApplicationProperties.class)
public class SpringBootVueJsApplication {

	public static void main(final String[] args) {
		SpringApplication.run(SpringBootVueJsApplication.class, args);
	}
}
