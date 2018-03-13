package me.grudzien.patryk;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class CreateFileOnActiveHerokuProfile {

	public static void main(final String[] args) {
		try {
			Files.readLines(new File("src/main/resources/application.yml"), Charsets.UTF_8)
			     .stream()
			     // filtering to find active profile
			     .filter(line -> line.contains("active: "))
			     // taking only active profile name after ": " character
			     .map(line -> line.substring(line.indexOf(':') + 1).trim())
			.findFirst()
			// if present generate file which will fire Heroku Maven Plugin and appropriate profiles
			.ifPresent(CreateFileOnActiveHerokuProfile::createFile);
		} catch (final IOException exception) {
			System.out.println(exception.getMessage());
		}
	}

	private static String createFile(final String activeProfileName) {
		try {
			// heroku-deployment-enabled
			Files.touch(new File(activeProfileName + "-enabled"));
		} catch (final IOException exception) {
			System.out.println(exception.getMessage());
		}
		return "File >>>> " + activeProfileName + "-enabled <<<< has been created!";
	}
}
