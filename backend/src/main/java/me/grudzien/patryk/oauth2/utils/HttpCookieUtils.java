package me.grudzien.patryk.oauth2.utils;

import lombok.extern.log4j.Log4j2;

import org.springframework.util.ObjectUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.util.Optional;
import java.util.stream.Stream;

@Log4j2
public final class HttpCookieUtils {

	/**
	 * Fetches a cookie from the request
	 */
	public static Optional<Cookie> fetchCookie(final HttpServletRequest request, final String cookieName) {
		final Cookie[] cookies = request.getCookies();
		return ObjectUtils.isEmpty(cookies) ? Optional.empty() : Stream.of(cookies)
		                                                               .filter(cookie -> cookie.getName().equals(cookieName))
		                                                               .findFirst();
	}
}
