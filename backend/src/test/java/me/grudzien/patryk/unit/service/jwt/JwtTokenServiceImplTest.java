package me.grudzien.patryk.unit.service.jwt;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.grudzien.patryk.PropertiesKeeper;
import me.grudzien.patryk.service.jwt.JwtTokenClaimsRetriever;
import me.grudzien.patryk.service.jwt.JwtTokenService;
import me.grudzien.patryk.service.jwt.impl.JwtTokenServiceImpl;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.util.web.RequestsDecoder;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceImplTest {

	@Mock
	private UserDetailsService userDetailsService;
	@Mock
	private LocaleMessagesCreator localeMessagesCreator;
	@Mock
	private PropertiesKeeper propertiesKeeper;
	@Mock
    private JwtTokenClaimsRetriever jwtTokenClaimsRetriever;
	@Mock
    private RequestsDecoder requestsDecoder;

	private JwtTokenService jwtTokenService;

	@BeforeEach
	void setUp() {
		jwtTokenService = new JwtTokenServiceImpl(userDetailsService, localeMessagesCreator, propertiesKeeper, jwtTokenClaimsRetriever, requestsDecoder);
	}
}
