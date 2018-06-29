package me.grudzien.patryk.service.registration;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.google.common.base.Preconditions;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;
import static me.grudzien.patryk.utils.log.LogMarkers.FLOW_MARKER;

import me.grudzien.patryk.domain.dto.registration.EmailDto;
import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.domain.entities.registration.EmailVerificationToken;
import me.grudzien.patryk.repository.registration.EmailVerificationTokenRepository;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.i18n.LocaleMessagesHelper;

@Log4j2
@Service
public class EmailServiceImpl implements EmailService {

	@Value("${spring.mail.username}")
	private String senderEmailAddress;

	private final JavaMailSender javaMailSender;
	private final SpringTemplateEngine templateEngine;
	private final EmailVerificationTokenRepository emailVerificationTokenRepository;
	private final LocaleMessagesCreator localeMessagesCreator;

	// JavaMailSender will be automatically autowired by spring boot if it finds configuration in application.yml file
	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	public EmailServiceImpl(final JavaMailSender javaMailSender, final SpringTemplateEngine templateEngine,
	                        final EmailVerificationTokenRepository emailVerificationTokenRepository,
	                        final LocaleMessagesCreator localeMessagesCreator) {

		Preconditions.checkNotNull(javaMailSender, "javaMailSender cannot be null!");
		Preconditions.checkNotNull(templateEngine, "templateEngine cannot be null!");
		Preconditions.checkNotNull(emailVerificationTokenRepository, "emailVerificationTokenRepository cannot be null!");
		Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");

		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
		this.emailVerificationTokenRepository = emailVerificationTokenRepository;
		this.localeMessagesCreator = localeMessagesCreator;
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
		final String htmlTemplate = templateEngine.process("email-template_" + LocaleMessagesHelper.getLocale(), context);

		final MimeMessage message = javaMailSender.createMimeMessage();
		try {
			// MimeMessageHelper allows to add attachments to the MimeMessage
			final MimeMessageHelper messageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
			                                                              StandardCharsets.UTF_8.name());
			messageHelper.setSubject(emailDto.getSubject());
			messageHelper.setText(htmlTemplate, Boolean.TRUE);
			messageHelper.setTo(emailDto.getTo());
			messageHelper.setFrom(senderEmailAddress, localeMessagesCreator.buildLocaleMessage("registration-email-personal-from-address"));

			javaMailSender.send(message);
		} catch (final MessagingException messagingException) {
			log.error(EXCEPTION_MARKER, "MessagingException thrown inside sendMessageUsingTemplate(), error message -> {}",
			          messagingException.getMessage());
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			log.error(EXCEPTION_MARKER, "UnsupportedEncodingException thrown inside sendMessageUsingTemplate(), error message -> {}",
			          unsupportedEncodingException.getMessage());
		}
	}

	@Override
	public void persistEmailVerificationToken(final CustomUser customUser, final String token) {
		emailVerificationTokenRepository.save(new EmailVerificationToken(token, customUser));
	}

	@Override
	public EmailVerificationToken generateNewEmailVerificationToken(final String existingEmailVerificationToken) {
		final EmailVerificationToken existingToken = emailVerificationTokenRepository.findByToken(existingEmailVerificationToken);
		log.info(FLOW_MARKER, "Found expired token for user: {}", existingToken.getCustomUser().getEmail());
		existingToken.updateToken(UUID.randomUUID().toString());
		log.info(FLOW_MARKER, "New token: {} generated successfully.", existingToken.getToken());
		return emailVerificationTokenRepository.save(existingToken);
	}

	@Override
	public EmailVerificationToken getEmailVerificationToken(final String emailVerificationToken) {
		return emailVerificationTokenRepository.findByToken(emailVerificationToken);
	}
}
