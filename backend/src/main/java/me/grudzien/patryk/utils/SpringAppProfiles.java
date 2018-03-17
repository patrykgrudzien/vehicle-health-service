package me.grudzien.patryk.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpringAppProfiles {
	DEV_HOME("dev-home"),
	DEV_OFFICE("dev-office"),
	HEROKU_DEPLOYMENT("heroku-deployment");

	private String ymlName;
}
