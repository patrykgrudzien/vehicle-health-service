package me.grudzien.patryk.integration.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Base64;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserAuthenticationControllerIT {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private CustomApplicationProperties customApplicationProperties;

	@Test
	public void testSuccessfulLogin() {
		final String password = Base64.getEncoder().encodeToString("admin".getBytes());
		// arrange
		final JwtAuthenticationRequest loginRequest = new JwtAuthenticationRequest("admin.root@gmail.com", password, "");

		// act
		final ResponseEntity<JwtAuthenticationResponse> response = testRestTemplate.postForEntity(
				customApplicationProperties.getEndpoints().getAuthentication().getRoot(), loginRequest, JwtAuthenticationResponse.class);

		// assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}
}
