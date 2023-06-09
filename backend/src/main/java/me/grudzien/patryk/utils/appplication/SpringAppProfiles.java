package me.grudzien.patryk.utils.appplication;

import static lombok.AccessLevel.NONE;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum SpringAppProfiles {

	DEV_HOME(YmlName.DEV_HOME),
	H2_IN_MEMORY(YmlName.H2_IN_MEMORY),
	HEROKU_DEPLOYMENT(YmlName.HEROKU_DEPLOYMENT);

	private final String ymlName;

	@NoArgsConstructor(access = NONE)
	public static final class YmlName {

		public static final String DEV_HOME = "dev-home";
		public static final String H2_IN_MEMORY = "h2-in-memory";
		public static final String HEROKU_DEPLOYMENT = "heroku-deployment";
	}
}
