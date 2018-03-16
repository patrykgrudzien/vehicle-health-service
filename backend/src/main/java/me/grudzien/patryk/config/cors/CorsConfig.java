package me.grudzien.patryk.config.cors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;

@Configuration
@Profile({"dev-home", "dev-office"})
public class CorsConfig {

	private final CustomApplicationProperties customApplicationProperties;

	@Autowired
	public CorsConfig(final CustomApplicationProperties customApplicationProperties) {
		this.customApplicationProperties = customApplicationProperties;
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(final CorsRegistry registry) {
				registry.addMapping("/**")
				        .allowedOrigins(customApplicationProperties.getCorsOrigins().getFrontEndModule());
			}
		};
	}
}
