package me.grudzien.patryk.utils.i18n;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import java.util.Locale;
import java.util.Objects;

@Component
public final class LocaleMessagesHelper {

	@Value("${custom.properties.messages-language.header}")
	private String messagesLanguageHeader;

	private LocaleMessagesHelper() {}

	public <T> Locale buildLocale(final T request) {
		if (request instanceof WebRequest) {
			return StringUtils.isEmpty(((WebRequest) request).getHeader(messagesLanguageHeader)) ? Locale.getDefault() :
					       new Locale(Objects.requireNonNull(((WebRequest) request).getHeader(messagesLanguageHeader)));
		} else if (request instanceof HttpServletRequest) {
			return StringUtils.isEmpty(((HttpServletRequest) request).getHeader(messagesLanguageHeader)) ? Locale.getDefault() :
					       new Locale(Objects.requireNonNull(((HttpServletRequest) request).getHeader(messagesLanguageHeader)));
		} else {
			return Locale.getDefault();
		}
	}

	public String removeSquareBracketsFromMessage(@NotNull final String message) {
		return !StringUtils.isEmpty(message) && (message.contains("[") || message.contains("]")) ?
				       message.replaceAll("\\[", "").replaceAll("]", "") : message;
	}
}
