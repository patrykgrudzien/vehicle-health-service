package me.grudzien.patryk.oauth2;

import lombok.extern.log4j.Log4j2;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Override
	protected String determineTargetUrl(final HttpServletRequest request, final HttpServletResponse response) {
		return super.determineTargetUrl(request, response);
	}
}
