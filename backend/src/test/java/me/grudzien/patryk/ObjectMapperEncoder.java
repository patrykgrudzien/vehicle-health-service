package me.grudzien.patryk;

import io.vavr.CheckedFunction0;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Optional;

import static lombok.AccessLevel.NONE;

@NoArgsConstructor(access = NONE)
public abstract class ObjectMapperEncoder {

	private static final Base64.Encoder encoder = Base64.getEncoder();
	private static final ObjectMapper objectMapper = new ObjectMapper();

	protected static String encodeNotNullValue(final String value) {
		return Optional.ofNullable(value)
		               .map(notEmptyValue -> encoder.encodeToString(notEmptyValue.getBytes()))
		               .orElse(null);
	}

	protected static String tryConvertObjectToJson(final Object value) {
		return CheckedFunction0.liftTry(() -> objectMapper.writeValueAsString(value))
		                       .apply()
		                       .get();
	}

	protected static <T> T tryConvertJsonToObject(final String json, final Class<T> returnType) {
		return CheckedFunction0.liftTry(() -> objectMapper.readValue(json, returnType))
		                       .apply()
		                       .get();
	}
}
