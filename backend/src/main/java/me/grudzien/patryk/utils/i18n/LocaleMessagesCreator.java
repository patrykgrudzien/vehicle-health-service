package me.grudzien.patryk.utils.i18n;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@Log4j2
@Component
public class LocaleMessagesCreator {

	private static final String DEFAULT_MESSAGE = "NO MESSAGE FOUND IN PROPERTIES FILE";

	private final MessageSource messageSource;
	private final LocaleMessagesHelper localeMessagesHelper;

	@Autowired
	public LocaleMessagesCreator(final MessageSource messageSource, final LocaleMessagesHelper localeMessagesHelper) {
		this.messageSource = messageSource;
		this.localeMessagesHelper = localeMessagesHelper;
	}

	@SuppressWarnings("SameParameterValue")
	public <T> String buildLocaleMessage(final String messageCode, final T webRequest) {
		return messageSource.getMessage(messageCode, new Object[]{}, DEFAULT_MESSAGE, localeMessagesHelper.buildLocale(webRequest));
	}

	@SuppressWarnings("SameParameterValue")
	public String buildLocaleMessageWithParam(final String messageCode, final WebRequest webRequest, @Nullable final Object param) {
		return localeMessagesHelper.removeSquareBracketsFromMessage(messageSource.getMessage(messageCode, new Object[]{param}, DEFAULT_MESSAGE,
		                                                                                     localeMessagesHelper.buildLocale(webRequest)));
	}

	@SafeVarargs
	@SuppressWarnings("SameParameterValue")
	public final String buildLocaleMessageWithParams(final String messageCode, final WebRequest webRequest,
	                                                 @Nullable final List<Object>... params) {
		return localeMessagesHelper.removeSquareBracketsFromMessage(messageSource.getMessage(messageCode, params, DEFAULT_MESSAGE,
		                                                                                     localeMessagesHelper.buildLocale(webRequest)));
	}
}