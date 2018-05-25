package me.grudzien.patryk.utils.i18n;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Locale;

@Log4j2
@Component
public class LocaleMessagesCreator {

	private static final String DEFAULT_MESSAGE = "NO MESSAGE FOUND IN PROPERTIES FILE";

	private final MessageSource messageSource;

	@Value("${custom.properties.messages-language.header}")
	private String messagesLanguageHeader;

	@Autowired
	public LocaleMessagesCreator(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@SuppressWarnings("SameParameterValue")
	public String buildLocaleMessage(final String messageCode, final WebRequest webRequest) {
		return messageSource.getMessage(messageCode, new Object[]{}, DEFAULT_MESSAGE, buildLocale(webRequest));
	}

	@SuppressWarnings("SameParameterValue")
	public String buildLocaleMessageWithParam(final String messageCode, final WebRequest webRequest, @Nullable final Object param) {
		return removeSquareBracketsFromMessage(messageSource.getMessage(messageCode, new Object[]{param}, DEFAULT_MESSAGE, buildLocale(webRequest)));
	}

	@SafeVarargs
	@SuppressWarnings("SameParameterValue")
	public final String buildLocaleMessageWithParams(final String messageCode, final WebRequest webRequest,
	                                                 @Nullable final List<Object>... params) {
		return removeSquareBracketsFromMessage(messageSource.getMessage(messageCode, params, DEFAULT_MESSAGE, buildLocale(webRequest)));
	}

	private Locale buildLocale(final WebRequest webRequest) {
		return StringUtils.isEmpty(webRequest.getHeader(messagesLanguageHeader)) ? Locale.getDefault() :
				       new Locale(webRequest.getHeader(messagesLanguageHeader));
	}

	private String removeSquareBracketsFromMessage(final String localeMessage) {
		return localeMessage.contains("[") || localeMessage.contains("]") ?
				       localeMessage.replaceAll("\\[", "").replaceAll("]", "") : localeMessage;
	}
}