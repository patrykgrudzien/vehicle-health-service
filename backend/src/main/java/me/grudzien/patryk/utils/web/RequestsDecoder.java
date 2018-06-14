package me.grudzien.patryk.utils.web;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Base64;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RequestsDecoder {

	public String decodeStringParam(final String arg) {

		if (!StringUtils.isEmpty(arg) && isParamEncoded(arg)) {
			return new String(Base64.getDecoder().decode(arg));
		} else {
			return arg;
		}
	}

	public Long decodeStringParamAndConvertToLong(final String arg) {
		if (!StringUtils.isEmpty(arg) && isParamEncoded(arg)) {
			return new Long(new String(Base64.getDecoder().decode(arg)));
		} else {
			return new Long(arg);
		}
	}

	private boolean isParamEncoded(final String param) {
		return org.apache.tomcat.util.codec.binary.Base64.isBase64(param);
	}
}
