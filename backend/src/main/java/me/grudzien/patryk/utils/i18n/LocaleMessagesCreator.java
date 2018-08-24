package me.grudzien.patryk.utils.i18n;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class LocaleMessagesCreator {

	private static final String DEFAULT_MESSAGE = "NO MESSAGE FOUND IN PROPERTIES FILE";

	@Setter
	@Getter
	private String createdMessage;

	private final MessageSource messageSource;
	private final LocaleMessagesHelper localeMessagesHelper;

	@Autowired
	public LocaleMessagesCreator(final MessageSource messageSource, final LocaleMessagesHelper localeMessagesHelper) {
		this.messageSource = messageSource;
		this.localeMessagesHelper = localeMessagesHelper;
	}

	public String buildLocaleMessage(final String messageCode) {
		setCreatedMessage(messageSource.getMessage(messageCode, new Object[]{}, DEFAULT_MESSAGE, LocaleMessagesHelper.getLocale()));
		return createdMessage;
	}

	public String buildLocaleMessageWithParam(final String messageCode, @Nullable final Object param) {
		setCreatedMessage(localeMessagesHelper.removeSquareBracketsFromMessage(messageSource.getMessage(messageCode, new Object[]{param}, DEFAULT_MESSAGE,
		                                                                                                LocaleMessagesHelper.getLocale())));
		return createdMessage;
	}

	@SafeVarargs
	public final String buildLocaleMessageWithParams(final String messageCode, @Nullable final List<Object>... params) {
		setCreatedMessage(localeMessagesHelper.removeSquareBracketsFromMessage(messageSource.getMessage(messageCode, params, DEFAULT_MESSAGE,
		                                                                                                LocaleMessagesHelper.getLocale())));
		return createdMessage;
	}
}