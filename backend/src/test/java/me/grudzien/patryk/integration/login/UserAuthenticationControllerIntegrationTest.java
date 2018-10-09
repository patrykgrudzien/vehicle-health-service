package me.grudzien.patryk.integration.login;

import static org.assertj.core.api.Assertions.assertThat;

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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserAuthenticationControllerIntegrationTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private CustomApplicationProperties customApplicationProperties;

	@Test
	public void testSuccessfulLogin() {
		final String password = Base64.getEncoder().encodeToString("admin".getBytes());
		// arrange
		final JwtAuthenticationRequest loginRequest = JwtAuthenticationRequest.Builder().email("admin.root@gmail.com").password(password).build();

		// act
		final ResponseEntity<JwtAuthenticationResponse> response = testRestTemplate.postForEntity(
				customApplicationProperties.getEndpoints().getAuthentication().getRoot(), loginRequest, JwtAuthenticationResponse.class);

		// assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}
}
