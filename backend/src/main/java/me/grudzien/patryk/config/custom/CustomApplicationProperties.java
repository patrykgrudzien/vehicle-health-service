package me.grudzien.patryk.config.custom;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "custom.properties")
public class CustomApplicationProperties {

	private Endpoints endpoints = new Endpoints();
	private CorsOrigins corsOrigins = new CorsOrigins();
	private Jwt jwt = new Jwt();

	@Getter
	@Setter
	public static class Jwt {
		private String header;
		private String secret;
		private Long expiration;
	}

	@Getter
	@Setter
	public static class Endpoints {

		private Server server = new Server();
		private Registration registration = new Registration();
		private Login login = new Login();
		private Logout logout = new Logout();
		private Heroku heroku = new Heroku();

		@Getter
		@Setter
		public static class Server {
			private String root;
			private String healthCheck;

			public String getRootHealthCheck() {
				return getRoot() + getHealthCheck();
			}
		}

		@Getter
		@Setter
		public static class Registration {
			private String root;
			private String registerUserAccount;
			private String confirmRegistration;
			private String confirmationUrl;
			private String resendEmailVerificationToken;
			private String confirmed;
			private String confirmedTokenNotFound;

			public String getRootRegisterUserAccount() {
				return getRoot() + getRegisterUserAccount();
			}

			public String getRootConfirmRegistration() {
				return getRoot() + getConfirmRegistration();
			}

			public String getRootConfirmationUrl() {
				return getRoot() + getConfirmationUrl();
			}

			public String getRootResendEmailVerificationToken() {
				return getRoot() + getResendEmailVerificationToken();
			}
		}

		@Getter
		@Setter
		public static class Login {
			private String root;
			private String failureUrl;
		}

		@Getter
		@Setter
		public static class Logout {
			private String root;
			private String successUrl;

			public String getRootSuccessUrl() {
				return getRoot() + getSuccessUrl();
			}
		}

		@Getter
		@Setter
		public static class Heroku {
			private String appUrl;
		}
	}

	@Getter
	@Setter
	public static class CorsOrigins {
		private String frontEndModule;
		private String backEndModule;
	}
}
