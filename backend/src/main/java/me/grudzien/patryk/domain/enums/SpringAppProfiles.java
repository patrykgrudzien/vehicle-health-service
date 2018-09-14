package me.grudzien.patryk.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpringAppProfiles {

	DEV_HOME(YmlName.DEV_HOME),
	DEV_OFFICE(YmlName.DEV_OFFICE),
	HEROKU_DEPLOYMENT(YmlName.HEROKU_DEPLOYMENT);

	private final String ymlName;

	public static final class YmlName {

		// disabling class object creation
		private YmlName() {
			throw new UnsupportedOperationException("Creating object of this class is not allowed!");
		}

		public static final String DEV_HOME = "dev-home";
		public static final String DEV_OFFICE = "dev-office";
		public static final String HEROKU_DEPLOYMENT = "heroku-deployment";
	}
}
