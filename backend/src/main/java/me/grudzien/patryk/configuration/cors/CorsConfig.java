package me.grudzien.patryk.configuration.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import static me.grudzien.patryk.utils.appplication.SpringAppProfiles.YmlName.DEV_HOME;
import static me.grudzien.patryk.utils.appplication.SpringAppProfiles.YmlName.H2_IN_MEMORY;

@Configuration
@Profile({DEV_HOME, H2_IN_MEMORY})
public class CorsConfig {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(final CorsRegistry registry) {
				registry.addMapping("/**")
				        .allowedMethods(GET.name(), POST.name(), PUT.name(), DELETE.name())
				        .allowedOrigins("*")
				        .allowedHeaders("*")
				        .exposedHeaders("X-Auth-Token", "Authorization", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials");
			}
		};
	}
}
