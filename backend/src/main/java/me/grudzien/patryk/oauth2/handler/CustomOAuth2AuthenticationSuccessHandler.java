package me.grudzien.patryk.oauth2.handler;

import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.grudzien.patryk.jwt.service.JwtTokenService;
import me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser;
import me.grudzien.patryk.oauth2.repository.CacheBasedOAuth2AuthorizationRequestRepository;
import me.grudzien.patryk.utils.web.CustomURLBuilder;
import me.grudzien.patryk.utils.web.MvcPattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

import static me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser.AccountStatus;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.GOOGLE_OAUTH2_RESOURCE_ROOT;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_LOGGED_IN_USING_GOOGLE;
import static me.grudzien.patryk.oauth2.resource.google.GoogleResourceDefinitions.USER_REGISTERED_USING_GOOGLE;

@Slf4j
@Component
public class CustomOAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	public static final String SHORT_LIVED_AUTH_TOKEN_NAME = "shortLivedAuthToken";

	private final CacheBasedOAuth2AuthorizationRequestRepository requestRepository;
	private final JwtTokenService jwtTokenService;

	@Autowired
	public CustomOAuth2AuthenticationSuccessHandler(final CacheBasedOAuth2AuthorizationRequestRepository requestRepository,
                                                    final JwtTokenService jwtTokenService) {
        checkNotNull(requestRepository, "requestRepository cannot be null!");
        checkNotNull(jwtTokenService, "jwtTokenService cannot be null!");

        this.requestRepository = requestRepository;
        this.jwtTokenService = jwtTokenService;
    }

	@Override
	protected String determineTargetUrl(final HttpServletRequest request, final HttpServletResponse response) {
		log.info(">>>> OAUTH2 <<<< determineTargetUrl()");
		final CustomOAuth2OidcPrincipalUser customOidcPrincipalUser =
                (CustomOAuth2OidcPrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		requestRepository.evictOAuth2AuthorizationRequestCache();
		return defineSuccessTargetURL(customOidcPrincipalUser);
	}

	private String defineSuccessTargetURL(final CustomOAuth2OidcPrincipalUser customOAuth2OidcPrincipalUser) {
		final AccountStatus userAccountStatus = customOAuth2OidcPrincipalUser.getAccountStatus();
		return Match(userAccountStatus).of(
				Case($(is(AccountStatus.REGISTERED)), () -> createUriTo(USER_REGISTERED_USING_GOOGLE)),
				Case($(is(AccountStatus.LOGGED)), () -> {
					final String shortLivedAuthToken = jwtTokenService.generateShortLivedToken(customOAuth2OidcPrincipalUser);
					final Tuple2<String, String> additionalUrlParameters = new Tuple2<>(SHORT_LIVED_AUTH_TOKEN_NAME, shortLivedAuthToken);
					return CustomURLBuilder.buildURL(createUriTo(USER_LOGGED_IN_USING_GOOGLE),
                                                     CustomURLBuilder.AdditionalParamsDelimiterType.REQUEST_PARAM, additionalUrlParameters);
				}),
				Case($(), StringUtils.EMPTY)
		);
	}

	// TODO: extract somewhere?
    private String createUriTo(final String resource) {
        return MvcPattern.of(GOOGLE_OAUTH2_RESOURCE_ROOT, resource);
    }
}
