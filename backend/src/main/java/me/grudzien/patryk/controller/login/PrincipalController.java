package me.grudzien.patryk.controller.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import me.grudzien.patryk.domain.dto.login.JwtUser;
import me.grudzien.patryk.service.security.MyUserDetailsService;
import me.grudzien.patryk.utils.security.JwtTokenUtil;

@RestController
@RequestMapping("/security")
public class PrincipalController {

	private final UserDetailsService userDetailsService;

	@Autowired
	public PrincipalController(@Qualifier(MyUserDetailsService.BEAN_NAME) final UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@GetMapping("/principal")
	public JwtUser getPrincipalUserFromAuthToken(final WebRequest webRequest) {
		final String jwtToken = JwtTokenUtil.Retriever.getJwtTokenFromRequest(webRequest);
		final String userEmail = JwtTokenUtil.Retriever.getUserEmailFromToken(jwtToken);
		return (JwtUser) userDetailsService.loadUserByUsername(userEmail);
	}

	@GetMapping("/firstname")
	@PreAuthorize("isAuthenticated()")
	public String getPrincipalUserFirstName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
