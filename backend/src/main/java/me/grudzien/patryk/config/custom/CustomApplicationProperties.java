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

	@Getter
	@Setter
	public static class Endpoints {

		private Api api = new Api();
		private Registration registration = new Registration();
		private Logout logout = new Logout();

		@Getter
		@Setter
		public static class Api {
			private String home;
			private String hello;

			public String getHomeHello() {
				return getHome() + getHello();
			}
		}

		@Getter
		@Setter
		public static class Registration {
			private String home;
			private String registerUserAccount;
			private String confirmRegistration;
			private String confirmationUrl;

			public String getHomeRegisterUserAccount() {
				return getHome() + getRegisterUserAccount();
			}

			public String getHomeConfirmRegistration() {
				return getHome() + getConfirmRegistration();
			}

			public String getHomeConfirmationUrl() {
				return getHome() + getConfirmationUrl();
			}
		}

		@Getter
		@Setter
		public static class Logout {
			private String home;
			private String successUrl;

			public String getHomeSuccessUrl() {
				return getHome() + getSuccessUrl();
			}
		}
	}

	@Getter
	@Setter
	public static class CorsOrigins {
		private String frontEndModule;
		private String backEndModule;
	}
}
