package me.grudzien.patryk.utils.web;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

import static me.grudzien.patryk.utils.common.Predicates.isNotAlpha;
import static me.grudzien.patryk.utils.common.Predicates.isNotEmpty;
import static me.grudzien.patryk.utils.common.Predicates.isNotNumeric;

@Log4j2
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RequestsDecoder {

    private static final String IS_BASE_64_ENCODED_PATTERN = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$";

	public String decodeStringParam(final String arg) {
	    return (isNotEmpty(arg) && isParamEncoded(arg) && isNotNumeric(arg)) ?
                toDecodedString(arg) : toPlainString(arg);
	}

    public Long decodeStringParamAndConvertToLong(final String arg) {
	    return (isNotEmpty(arg) && isParamEncoded(arg)) ?
                toDecodedLong(arg) : toPlainLong(arg);
	}

    public boolean isParamEncoded(final String param) {
        return (nonNull(param) && isNotEmpty(param)) &&
               (isBase64(param) && matchesBase64EncodedPattern(param) && isNotAlpha(param));
    }

    private String toPlainString(final String arg) {
        log.info("String param is NOT encoded!");
        return arg;
    }

    private String toDecodedString(final String arg) {
        log.info("Decoding String param -> {}.", arg);
        return new String(Base64.getDecoder().decode(arg));
    }

    private Long toPlainLong(final String arg) {
        log.info("String param is NOT encoded!");
        return Long.valueOf(arg);
    }

    private Long toDecodedLong(final String arg) {
        log.info("Decoding String param -> {} and converting to Long.", arg);
        return Long.valueOf(new String(Base64.getDecoder().decode(arg)));
    }

	private boolean isBase64(final String param) {
	    return org.apache.tomcat.util.codec.binary.Base64.isBase64(param);
    }

    private boolean matchesBase64EncodedPattern(final String param) {
        final Pattern pattern = Pattern.compile(IS_BASE_64_ENCODED_PATTERN);
        final Matcher matcher = pattern.matcher(param);
        return matcher.matches();
    }
}
