package me.grudzien.patryk.utils.web;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RequestsDecoder {

    private static final String IS_BASE_64_ENCODED_PATTERN = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$";

	public String decodeStringParam(final String arg) {
		if (!StringUtils.isEmpty(arg) && isParamEncoded(arg) && !org.apache.commons.lang3.StringUtils.isNumeric(arg)) {
		    log.info("Decoding String param -> {}.", arg);
			return new String(Base64.getDecoder().decode(arg));
		} else {
		    log.info("String param is NOT encoded!");
			return arg;
		}
	}

	public Long decodeStringParamAndConvertToLong(final String arg) {
		if (!StringUtils.isEmpty(arg) && isParamEncoded(arg)) {
            log.info("Decoding String param -> {} and converting to Long.", arg);
			return new Long(new String(Base64.getDecoder().decode(arg)));
		} else {
            log.info("String param is NOT encoded!");
			return new Long(arg);
		}
	}

	public boolean isParamEncoded(final String param) {
		return (param != null && !param.equals("")) && (isBase64(param) && matchesBase64EncodedPattern(param) && isNotAlpha(param));
	}

	private boolean isBase64(final String param) {
	    return org.apache.tomcat.util.codec.binary.Base64.isBase64(param);
    }

    private boolean matchesBase64EncodedPattern(final String param) {
        final Pattern pattern = Pattern.compile(IS_BASE_64_ENCODED_PATTERN);
        final Matcher matcher = pattern.matcher(param);
        return matcher.matches();
    }

    private boolean isNotAlpha(final String param) {
	    return !org.apache.commons.lang3.StringUtils.isAlpha(param);
    }
}
