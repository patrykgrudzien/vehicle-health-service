package me.grudzien.patryk.util.jwt;

import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import me.grudzien.patryk.domain.dto.login.JwtUser;

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

	public static class Validator {

		private static Boolean isCreatedBeforeLastPasswordReset(final Date created, final Date lastPasswordReset) {
			return (lastPasswordReset != null && created.before(lastPasswordReset));
		}

		public static Boolean validateAccessToken(final String token, final UserDetails userDetails) {
			final JwtUser user = (JwtUser) userDetails;
//			final String userEmail = Retriever.getUserEmailFromToken(token);
			// TODO: fix me for different time zones
//			final Date created = Retriever.getIssuedAtDateFromToken(token);
//			return (userEmail.equals(user.getEmail()) && !isTokenExpired(token));
//			return (userEmail.equals(user.getEmail()) && !isTokenExpired(token) && !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate()));
            return false;
		}
	}
}
