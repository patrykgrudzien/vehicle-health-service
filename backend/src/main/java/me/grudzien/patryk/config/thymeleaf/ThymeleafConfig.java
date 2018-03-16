package me.grudzien.patryk.config.thymeleaf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;

/**
 * Need to tell Thymeleaf where the email templates are located.
 * We can set a prefix and suffix to configure where Thymeleaf will search for the HTML email templates.
 * Next, we need to create a "SpringTemplateEngine" which is responsible for resolving the correct templating engine.
 */
@Configuration
public class ThymeleafConfig {

	@Bean
	public SpringResourceTemplateResolver htmlTemplateResolver() {
		final SpringResourceTemplateResolver emailTemplateResolver = new SpringResourceTemplateResolver();
		emailTemplateResolver.setPrefix("classpath:/templates/email/");
		emailTemplateResolver.setSuffix(".html");
		emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
		emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
		return emailTemplateResolver;
	}

	@Bean
	public SpringTemplateEngine springTemplateEngine() {
		final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.addTemplateResolver(htmlTemplateResolver());
		return templateEngine;
	}
}
