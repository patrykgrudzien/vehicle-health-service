package me.grudzien.patryk.service.security.impl;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import java.util.Optional;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.exception.security.CannotRefreshAccessTokenException;
import me.grudzien.patryk.exception.security.NoRefreshTokenProvidedException;
import me.grudzien.patryk.service.security.JwtTokenService;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.util.jwt.JwtTokenUtil.Creator;
import me.grudzien.patryk.util.jwt.JwtTokenUtil.Retriever;

@Log4j2
@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final UserDetailsService userDetailsService;
    private final LocaleMessagesCreator localeMessagesCreator;

    @Autowired
    public JwtTokenServiceImpl(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService,
                               final LocaleMessagesCreator localeMessagesCreator) {
        Preconditions.checkNotNull(userDetailsService, "userDetailsService cannot be null!");
        Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
        this.userDetailsService = userDetailsService;
        this.localeMessagesCreator = localeMessagesCreator;
    }

    @Override
    public String generateAccessToken(final JwtAuthenticationRequest authenticationRequest, final Device device) {
        return "";
    }

    @Override
    public String generateRefreshToken(final JwtAuthenticationRequest authenticationRequest, final Device device) {
        return "";
    }

    @Override
    public String refreshAccessToken(final JwtAuthenticationRequest authenticationRequest, final Device device) {
        return Optional.ofNullable(authenticationRequest.getRefreshToken())
                       .map(refreshToken -> {
                           final String email = Retriever.getUserEmailFromToken(refreshToken);
                           return Optional.ofNullable(((JwtUser) userDetailsService.loadUserByUsername(email)))
                                          .map(jwtUser -> Creator.generateAccessToken(jwtUser, device))
                                          .orElseThrow(() -> new CannotRefreshAccessTokenException(
                                                  localeMessagesCreator.buildLocaleMessageWithParam("cannot-refresh-access-token-for-provided-email", email)));
                       })
                       .orElseThrow(() -> new NoRefreshTokenProvidedException(
                               localeMessagesCreator.buildLocaleMessage("no-refresh-token-provided")));
    }
}
