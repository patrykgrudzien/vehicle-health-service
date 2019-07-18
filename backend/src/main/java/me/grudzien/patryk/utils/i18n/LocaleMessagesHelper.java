package me.grudzien.patryk.utils.i18n;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import me.grudzien.patryk.jwt.service.JwtTokenClaimsRetriever;
import me.grudzien.patryk.jwt.service.JwtTokenValidator;

@Log4j2
@Component
public class LocaleMessagesHelper {

	@Getter
	@Setter
	private Locale locale = Locale.getDefault();

	private static final String MESSAGES_LANGUAGE_HEADER = "Language";

	String removeSquareBracketsFromMessage(@NotNull final String message) {
		return !StringUtils.isEmpty(message) && (message.contains("[") || message.contains("]")) ?
				       message.replaceAll("\\[", "").replaceAll("]", "") : message;
	}

	private <T> String getLanguageHeaderFromRequest(final T request) {
		if (request instanceof WebRequest) {
			return Optional.ofNullable(((WebRequest) request).getHeader(MESSAGES_LANGUAGE_HEADER)).orElse(getLocale().getLanguage());
		} else if (request instanceof HttpServletRequest) {
			return Optional.ofNullable(((HttpServletRequest) request).getHeader(MESSAGES_LANGUAGE_HEADER)).orElse(getLocale().getLanguage());
		} else {
			return getLocale().getLanguage();
		}
	}

	private <T> void buildLocale(final T request) {
		final String languageHeaderFromRequest = getLanguageHeaderFromRequest(request);
		if (request instanceof WebRequest) {
			setLocale(StringUtils.isEmpty(languageHeaderFromRequest) ? Locale.getDefault() :
					          new Locale(Objects.requireNonNull(languageHeaderFromRequest)));
		} else if (request instanceof HttpServletRequest) {
			setLocale(StringUtils.isEmpty(languageHeaderFromRequest) ? Locale.getDefault() :
					          new Locale(Objects.requireNonNull(languageHeaderFromRequest)));
		}
	}

	private <T> boolean isLocaleChanged(final T request) {
		return !getLanguageHeaderFromRequest(request).equals(getLocale().getLanguage());
	}

	/**
	 * This method is called in
	 * {@link me.grudzien.patryk.configuration.filters.LocaleDeterminerFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)} method which is the filter run on each request in
	 * the chain and locale is set based on the header coming from UI.
	 *
	 * Filters' order with appropriate comments is specified in:
	 * {@link #addTokenAuthenticationFilters(org.springframework.security.config.annotation.web.builders.HttpSecurity, org.springframework.security.core.userdetails.UserDetailsService, LocaleMessagesCreator, JwtTokenClaimsRetriever, JwtTokenValidator, LocaleMessagesHelper)}
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
