package me.grudzien.patryk.service;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;

import me.grudzien.patryk.domain.dto.EmailDto;

@Log4j
@Service
public class EmailServiceImpl implements EmailService {

	@Value("${spring.mail.username}")
	private String senderEmailAddress;

	private final JavaMailSender javaMailSender;
	private final SpringTemplateEngine templateEngine;

	// JavaMailSender will be automatically autowired by spring boot if it finds configuration in application.yml file
	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	public EmailServiceImpl(final JavaMailSender javaMailSender, final SpringTemplateEngine templateEngine) {
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
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
		final String htmlTemplate = templateEngine.process("email-template", context);

		final MimeMessage message = javaMailSender.createMimeMessage();
		try {
			// MimeMessageHelper allows to add attachments to the MimeMessage
			final MimeMessageHelper messageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
			                                                              StandardCharsets.UTF_8.name());
			messageHelper.setSubject(emailDto.getSubject());
			messageHelper.setText(htmlTemplate, Boolean.TRUE);
			messageHelper.setTo(emailDto.getTo());
			messageHelper.setFrom(senderEmailAddress);

			javaMailSender.send(message);
		} catch (final MessagingException messagingException) {
			log.error(messagingException);
		}
	}
}
