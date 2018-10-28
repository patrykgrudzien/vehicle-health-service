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
	private MessagesLanguage messagesLanguage = new MessagesLanguage();

	@Getter
	@Setter
	public static class MessagesLanguage {
		private String header;
	}

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

		private Registration registration = new Registration();
		private Authentication authentication = new Authentication();
		private Heroku heroku = new Heroku();
		private VehicleResource vehicleResource = new VehicleResource();
		private OAuth2 oAuth2 = new OAuth2();

		@Getter
		@Setter
		public static class OAuth2 {
			private String loginPage;
			private Long shortLivedMillis;
			private String userLoggedInUsingGoogle;
			private String userNotFound;
			private String userRegisteredUsingGoogle;
			private String userAccountAlreadyExists;
			private String failureTargetUrl;
		}

		@Getter
		@Setter
		public static class VehicleResource {
			private String root;
			private String getVehicle;
			private String getCurrentMileage;
			private String updateCurrentMileage;
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
			private String confirmedTokenExpired;
			private String userAlreadyEnabled;
		}

		@Getter
		@Setter
		public static class Authentication {
			private String root;
			private String failureUrl;
			private String principalUser;
			private String refreshToken;
		}

		@Getter
		@Setter
		public static class Heroku {
			private String contextPath;
		}
	}

	@Getter
	@Setter
	public static class CorsOrigins {
		private String frontEndModule;
		private String backEndModule;
	}
}
