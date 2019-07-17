package me.grudzien.patryk.jwt.utils;

import org.springframework.mobile.device.Device;

public final class JwtTokenHelper {

    private JwtTokenHelper() {
        throw new UnsupportedOperationException("Creating object of this class is not allowed!");
    }

	public static String generateAudience(final Device device) {
		String audience = JwtTokenConstants.AUDIENCE_UNKNOWN;
		if (device.isNormal()) {
			audience = JwtTokenConstants.AUDIENCE_WEB;
		} else if (device.isTablet()) {
			audience = JwtTokenConstants.AUDIENCE_TABLET;
		} else if (device.isMobile()) {
			audience = JwtTokenConstants.AUDIENCE_MOBILE;
		}
		return audience;
	}
}
