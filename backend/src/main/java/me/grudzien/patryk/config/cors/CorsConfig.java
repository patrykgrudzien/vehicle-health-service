package me.grudzien.patryk.config.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import me.grudzien.patryk.domain.enums.SpringAppProfiles;

@Configuration
@Profile({SpringAppProfiles.YmlName.DEV_HOME, SpringAppProfiles.YmlName.H2_IN_MEMORY})
public class CorsConfig {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(final CorsRegistry registry) {
				registry.addMapping("/**")
				        .allowedMethods("GET", "POST", "PUT", "DELETE")
				        .allowedOrigins("*")
				        .allowedHeaders("*")
				        .exposedHeaders("X-Auth-Token", "Authorization", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials");
			}
		};
	}
}
