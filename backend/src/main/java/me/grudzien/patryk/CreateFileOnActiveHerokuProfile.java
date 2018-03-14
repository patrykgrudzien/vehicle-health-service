package me.grudzien.patryk;

import lombok.extern.log4j.Log4j;

import org.springframework.core.io.FileSystemResource;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.IOException;

/**
 * This class is used by exec-maven-plugin which is gonna execute this main() method and create file which is a trigger for
 * heroku-maven-plugin to start deployment process on Heroku.
 */
@Log4j
public class CreateFileOnActiveHerokuProfile {

	private static final String BACKEND_MODULE_NAME = "backend";
	private static final String HEROKU_DEPLOYMENT_PROFILE_NAME = "heroku-deployment";

	public static void main(final String[] args) {
		try {
			Files.readLines(new FileSystemResource(resolveApplicationYmlPath()).getFile(), Charsets.UTF_8)
			     .stream()
			     // filtering to find active profile
			     .filter(line -> line.contains("active: "))
			     // display in console name of active profile
			     .peek(activeProfileName -> System.out.println("ACTIVE PROFILE >>>> " + activeProfileName))
			     // taking only active profile name after ": " character
			     .map(line -> line.substring(line.indexOf(':') + 1).trim())
			     // create profile only for "heroku-deployment" profile
			     .filter(profileName -> profileName.equals(HEROKU_DEPLOYMENT_PROFILE_NAME))
			.findFirst()
			// if present generate file which will fire Heroku Maven Plugin and appropriate profiles
			.ifPresent(CreateFileOnActiveHerokuProfile::createFile);
		} catch (final IOException exception) {
			log.error(exception.getMessage());
		}
	}

	private static String createFile(final String activeProfileName) {
		final String successMessage = "File >>>> " + activeProfileName + "-enabled <<<< has been created!";
		try {
			// heroku-deployment-enabled
			Files.touch(new FileSystemResource(resolveHerokuDeploymentEnabledFileOutputPath(activeProfileName)).getFile());
			log.info(successMessage);
		} catch (final IOException exception) {
			log.error(exception.getMessage());
		}
		return "createFile() method executed.";
	}

	private static String resolveApplicationYmlPath() throws IOException {
		final FileSystemResource fileSystemResource = new FileSystemResource("src/main/resources/application.yml");
		final String path = fileSystemResource.getURI().getPath();

		if (path.contains(BACKEND_MODULE_NAME) || path.equals("")) {
			return "src/main/resources/application.yml";
		} else {
			return BACKEND_MODULE_NAME + "/src/main/resources/application.yml";
		}
	}

	private static String resolveHerokuDeploymentEnabledFileOutputPath(final String activeProfileName) throws IOException {
		if (new FileSystemResource("").getURI().getPath().contains(BACKEND_MODULE_NAME)) {
			return "../" + activeProfileName + "-enabled";
		} else {
			return activeProfileName + "-enabled";
		}
	}
}
