package me.grudzien.patryk.utils.i18n;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Component
public final class LocaleMessagesHelper {

	@Value("${custom.properties.messages-language.header}")
	private String messagesLanguageHeader;

	@Getter
	@Setter
	private Locale locale = Locale.getDefault();

	// cannot throw new UnsupportedOperationException("Creating object of this class is not allowed!") here -> Spring requires it!
	private LocaleMessagesHelper() {}

	public String removeSquareBracketsFromMessage(@NotNull final String message) {
		return !StringUtils.isEmpty(message) && (message.contains("[") || message.contains("]")) ?
				       message.replaceAll("\\[", "").replaceAll("]", "") : message;
	}

	private <T> String getLanguageHeaderFromRequest(final T request) {
		if (request instanceof WebRequest) {
			return Optional.ofNullable(((WebRequest) request).getHeader(messagesLanguageHeader)).orElse(getLocale().getLanguage());
		} else if (request instanceof HttpServletRequest) {
			return Optional.ofNullable(((HttpServletRequest) request).getHeader(messagesLanguageHeader)).orElse(getLocale().getLanguage());
		} else {
			return getLocale().getLanguage();
		}
	}

	private <T> void buildLocale(final T request) {
		if (request instanceof WebRequest) {
			locale = StringUtils.isEmpty(getLanguageHeaderFromRequest(request)) ? Locale.getDefault() :
					         new Locale(Objects.requireNonNull(getLanguageHeaderFromRequest(request)));
		} else if (request instanceof HttpServletRequest) {
			locale = StringUtils.isEmpty(getLanguageHeaderFromRequest(request)) ? Locale.getDefault() :
					         new Locale(Objects.requireNonNull(getLanguageHeaderFromRequest(request)));
		}
	}

	private <T> boolean isLocaleChanged(final T request) {
		return !getLanguageHeaderFromRequest(request).equals(getLocale().getLanguage());
	}

	/**
	 * This method is called in
	 * {@link me.grudzien.patryk.config.security.ServletExceptionHandlerFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)} method
	 * which is the first filter in the chain and locale is set based on the header coming from UI.
	 *
	 * Filters' order is specified in:
	 * {@link me.grudzien.patryk.config.security.SecurityConfig#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)}
	 *
	 * Later subsequent calls to {@link me.grudzien.patryk.utils.i18n.LocaleMessagesCreator#buildLocaleMessage(String)} related methods
	 * are performed to create i18n messages.
	 */
	public <T> void determineApplicationLocale(final T request) {
		if (isLocaleChanged(request)) {
			buildLocale(request);
		} else {
			setLocale(new Locale(getLocale().getLanguage()));
		}
	}
}
