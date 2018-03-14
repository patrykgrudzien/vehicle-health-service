package me.grudzien.patryk;

import lombok.extern.log4j.Log4j;

import org.springframework.core.io.FileSystemResource;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import java.io.IOException;
import java.util.List;

/**
 * This class is used by exec-maven-plugin which is gonna execute this main() method and create files which are triggers for
 * heroku-maven-plugin to start deployment process on Heroku.
 */
@Log4j
public class CreateFileOnActiveHerokuProfile {

	private static final String FRONTEND_MODULE_NAME = "frontend";
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

	private static String resolveApplicationYmlPath() throws IOException {
		final FileSystemResource fileSystemResource = new FileSystemResource("src/main/resources/application.yml");
		final String path = fileSystemResource.getURI().getPath();

		if (path.contains(BACKEND_MODULE_NAME)) {
			return "src/main/resources/application.yml";
		} else {
			return BACKEND_MODULE_NAME + "/src/main/resources/application.yml";
		}
	}

	private static List<String> resolveHerokuDeploymentEnabledFilesOutputPaths(final String activeProfileName) {

		String projectPath = "";
		try {
			projectPath = new FileSystemResource("").getURI().getPath();
			log.info("Project path found: " + projectPath);
		} catch (final IOException exception) {
			log.error("Cannot find project path !!!");
		}
		final List<String> generatedFilesPaths = Lists.newArrayList();

		if (projectPath.contains(FRONTEND_MODULE_NAME)) {
			generatedFilesPaths.add(activeProfileName + "-" + FRONTEND_MODULE_NAME + "-enabled");
			generatedFilesPaths.add("../" + BACKEND_MODULE_NAME + "/" + activeProfileName + "-" + BACKEND_MODULE_NAME + "-enabled");
		}
		if (projectPath.contains(BACKEND_MODULE_NAME)) {
			generatedFilesPaths.add(activeProfileName + "-" + BACKEND_MODULE_NAME + "-enabled");
			generatedFilesPaths.add("../" + FRONTEND_MODULE_NAME + "/" + activeProfileName + "-" + FRONTEND_MODULE_NAME + "-enabled");
		}
		else {
			generatedFilesPaths.add(FRONTEND_MODULE_NAME + "/" + activeProfileName + "-" + FRONTEND_MODULE_NAME + "-enabled");
			generatedFilesPaths.add(BACKEND_MODULE_NAME + "/" + activeProfileName + "-" + BACKEND_MODULE_NAME + "-enabled");
		}
		return generatedFilesPaths;
	}

	private static String createFile(final String activeProfileName) {
		try {
			resolveHerokuDeploymentEnabledFilesOutputPaths(activeProfileName).forEach(path -> {
				try {
					Files.touch(new FileSystemResource(path).getFile());
					log.info("File >>>> " + path + " <<<< has been created!");
				} catch (final IOException exception) {
					log.error(exception.getMessage());
				}
			});
		} finally {
			log.info("Files(s) created. TASK COMPLETED.");
		}
		return "createFile() method executed.";
	}
}
