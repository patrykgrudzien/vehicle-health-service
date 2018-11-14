package me.grudzien.patryk.oauth2.util;

import org.springframework.util.StringUtils;

import org.apache.logging.log4j.util.Strings;

import com.google.common.collect.Sets;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import me.grudzien.patryk.util.web.CustomURLBuilder;

public class URLParser {

	private static final Set<String> HTTP_PROTOCOLS = Sets.newHashSet("http://", "https://");
	private static final Function<String[], String> GET_ARRAY_LAST_ELEMENT = array -> CustomURLBuilder.PATH_VARIABLE_DELIMITER + array[array.length - 1];

	public static Optional<String> retrieveEndpointFromURL(final String url) {
		return StringUtils.isEmpty(url) ? Optional.empty() : HTTP_PROTOCOLS.stream()
		                                                                   .map(protocol -> url.contains(protocol) ? url.replace(protocol, Strings.EMPTY) : null)
		                                                                   .filter(Objects::nonNull)
		                                                                   .map(cleanUrl -> cleanUrl.split(CustomURLBuilder.PATH_VARIABLE_DELIMITER))
		                                                                   .collect(Collectors.toList())
		                                                                   .stream()
		                                                                   .map(GET_ARRAY_LAST_ELEMENT)
		                                                                   .findFirst();
	}
}
