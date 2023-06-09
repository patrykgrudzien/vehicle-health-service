package me.grudzien.patryk.registration.service.impl;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import me.grudzien.patryk.registration.model.dto.EmailDto;
import me.grudzien.patryk.registration.service.EmailClientService;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.i18n.LocaleMessagesHelper;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class EmailClientServiceImpl implements EmailClientService {

	@Value("${spring.mail.username}")
	private String senderEmailAddress;

	private final JavaMailSender javaMailSender;
	private final SpringTemplateEngine templateEngine;
	private final LocaleMessagesCreator localeMessagesCreator;
	private final LocaleMessagesHelper localeMessagesHelper;

	// JavaMailSender will be automatically autowired by spring boot if it finds configuration in application.yml file
	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	public EmailClientServiceImpl(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") final JavaMailSender javaMailSender,
                                  final SpringTemplateEngine templateEngine, final LocaleMessagesCreator localeMessagesCreator,
                                  final LocaleMessagesHelper localeMessagesHelper) {

		checkNotNull(javaMailSender, "javaMailSender cannot be null!");
		checkNotNull(templateEngine, "templateEngine cannot be null!");
		checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
		checkNotNull(localeMessagesHelper, "localeMessagesHelper cannot be null!");

		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
		this.localeMessagesCreator = localeMessagesCreator;
		this.localeMessagesHelper = localeMessagesHelper;
	}

	@Override
	public void sendSimpleMessage(final EmailDto emailDto) {
		final SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject(emailDto.getSubject());
		message.setText(emailDto.getContent());
		message.setTo(emailDto.getTo());
		message.setFrom(senderEmailAddress);

		javaMailSender.send(message);
	}

	@Override
	public void sendMessageUsingTemplate(final EmailDto emailDto) {
		// context object holds all the (key, value) pairs we can use inside the template
		final Context context = new Context();
		context.setVariables(emailDto.getTemplatePlaceholders());

		// we process the HTML Thymeleaf Email Template by calling process() method
		final String htmlTemplate = templateEngine.process("email-template_" + localeMessagesHelper.getLocale(), context);

		final MimeMessage message = javaMailSender.createMimeMessage();

		Try.run(() -> {
			// MimeMessageHelper allows to add attachments to the MimeMessage
			final MimeMessageHelper messageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
			                                                              StandardCharsets.UTF_8.name());
			messageHelper.setSubject(emailDto.getSubject());
			messageHelper.setText(htmlTemplate, Boolean.TRUE);
			messageHelper.setTo(emailDto.getTo());
			messageHelper.setFrom(senderEmailAddress, localeMessagesCreator.buildLocaleMessage("registration-email-personal-from-address"));

			javaMailSender.send(message);
		})
		   .onSuccess(successVoid -> log.info("An registration e-mail has been successfully sent."))
		   .onFailure(throwable -> Match(throwable).of(
		   		Case($(instanceOf(MessagingException.class)),
			         MessagingException -> run(() -> log.error("MessagingException thrown inside sendMessageUsingTemplate(), error message -> {}",
			                                                   MessagingException.getMessage()))),
			    Case($(instanceOf(UnsupportedEncodingException.class)),
			         UnsupportedEncodingException -> run(() -> log.error("UnsupportedEncodingException thrown inside sendMessageUsingTemplate(), error message -> {}",
			                                                             UnsupportedEncodingException.getMessage())))
		   ));
	}
}
