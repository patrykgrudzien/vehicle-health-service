package me.grudzien.patryk.utils.web;

import static org.springframework.util.ObjectUtils.isEmpty;

import io.vavr.Tuple2;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.lang.NonNull;

import org.apache.logging.log4j.util.Strings;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.grudzien.patryk.utils.web.CustomURLBuilder.URLParamType.NONE;
import static me.grudzien.patryk.utils.web.CustomURLBuilder.URLParamType.PATH_VARIABLE;
import static me.grudzien.patryk.utils.web.CustomURLBuilder.URLParamType.REQUEST_PARAM;

public class CustomURLBuilder {

	public static final String PATH_VARIABLE_DELIMITER = "/";
	private static final Tuple2<String, String> REQUEST_PARAM_DELIMITERS = new Tuple2<>("?", "=");

	@Getter
	@AllArgsConstructor
	public enum URLParamType {
		PATH_VARIABLE {
			@SafeVarargs
			@Override
			final String delimiterWith(final Tuple2<String, String>... additionalParameters) {
				return !isEmpty(additionalParameters) ? Stream.of(additionalParameters)
				                                              .map(additionalParameter -> PATH_VARIABLE_DELIMITER + additionalParameter._2())
				                                              .collect(Collectors.joining()) : Strings.EMPTY;
			}
		},
		REQUEST_PARAM {
			@SafeVarargs
			@Override
			final String delimiterWith(final Tuple2<String, String>... additionalParameters) {
				return !isEmpty(additionalParameters) ? Stream.of(additionalParameters)
				                                              .map(additionalParameter -> PATH_VARIABLE_DELIMITER +
				                                                                          REQUEST_PARAM_DELIMITERS._1() + additionalParameter._1() +
				                                                                          REQUEST_PARAM_DELIMITERS._2() + additionalParameter._2())
				                                              .collect(Collectors.joining()) : Strings.EMPTY;
			}
		},
		NONE {
			@SafeVarargs
			@Override
			final String delimiterWith(final Tuple2<String, String>... additionalParameters) {
				return "";
			}
		};
		// build path with additional parameters (if present) separated by delimiter
		@SuppressWarnings("unchecked")
		abstract String delimiterWith(final Tuple2<String, String>... additionalParameters);
	}

	@SafeVarargs
	public static String buildURL(@NonNull final String context, @NonNull final URLParamType urlParamType,
	                              final Tuple2<String, String>... additionalParameters) {
		if (urlParamType == NONE || isEmpty(additionalParameters)) {
			return context;
		} else if (!isEmpty(additionalParameters)) {
			switch (urlParamType) {
				case PATH_VARIABLE:
					return context + PATH_VARIABLE.delimiterWith(additionalParameters);
				case REQUEST_PARAM:
					return context + REQUEST_PARAM.delimiterWith(additionalParameters);
			}
		}
		return null;
	}
}
