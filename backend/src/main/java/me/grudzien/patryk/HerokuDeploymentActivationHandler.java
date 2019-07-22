package me.grudzien.patryk;

import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import org.springframework.core.io.FileSystemResource;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.collect.Lists.newArrayList;
import static io.vavr.CheckedFunction0.liftTry;

import static me.grudzien.patryk.utils.appplication.SpringAppProfiles.HEROKU_DEPLOYMENT;

/**
 * This class is used by (exec-maven-plugin) which is gonna execute
 * {@link me.grudzien.patryk.HerokuDeploymentActivationHandler#main(String[])}
 * method and create activation files which trigger (heroku-maven-plugin) to start deployment process on Heroku.
 */
@Log4j2
public final class HerokuDeploymentActivationHandler {

	private static final String FRONTEND_MODULE_NAME = "frontend";
	private static final String BACKEND_MODULE_NAME = "backend";
	private static final String GO_DIRECTORY_UP = "../";
	private static final String SLASH = "/";
	private static final String DASH = "-";
	private static final String ENABLED = "-enabled";
	private static final String APPLICATION_YML_LOCATION = "src/main/resources/application.yml";

	public static void main(final String[] args) {
		liftTry(() -> FileUtils.readLines(new FileSystemResource(resolveApplicationYmlPath()).getFile(), UTF_8)).apply()
						.onSuccess(lines -> lines.stream()
		                                  .filter(line -> line.contains("active: "))
		                                  // TODO: improve log statement according to some common (project level) pattern
		                                  .peek(activeProfileName -> log.debug("ACTIVE PROFILE >>>> " + activeProfileName))
		                                  // taking only active profile name after ": " character
		                                  .map(line -> line.substring(line.indexOf(':') + 1).trim())
		                                  .filter(profileName -> HEROKU_DEPLOYMENT.getYmlName().equals(profileName))
		                                  .findFirst()
		                                  .ifPresent(HerokuDeploymentActivationHandler::createActivationFiles))
						.onFailure(throwable -> log.error(throwable.getMessage()));
	}

	private static String resolveApplicationYmlPath() throws IOException {
		return new FileSystemResource(APPLICATION_YML_LOCATION).getURI().getPath().contains(BACKEND_MODULE_NAME) ?
				       APPLICATION_YML_LOCATION :
				       BACKEND_MODULE_NAME + SLASH + APPLICATION_YML_LOCATION;
	}

	private static void createActivationFiles(final String activeProfileName) {
		getPathsOfHerokuDeploymentActivationFiles(activeProfileName)
				.forEach(path -> Try.run(() -> FileUtils.touch(new FileSystemResource(path).getFile()))
				                    // TODO: improve log statement according to some common (project level) pattern
				                    .onSuccess(voidResult -> log.debug("File >>>> {} <<<< has been created! TASK COMPLETED.", path))
				                    .onFailure(throwable -> log.error(throwable.getMessage())));
	}

	private static List<String> getPathsOfHerokuDeploymentActivationFiles(final String activeProfileName) {
		final String pwd = liftTry(() -> new FileSystemResource("").getURI().getPath()).apply()
				.map(Function.identity())
				.onFailure(throwable -> log.error("Cannot find project path !!!"))
				.get();

		final List<String> pathsToActivationFiles = newArrayList();
		if (pwd.contains(FRONTEND_MODULE_NAME)) {
			pathsToActivationFiles.add(activeProfileName + "-" + FRONTEND_MODULE_NAME + ENABLED);
			pathsToActivationFiles.add(GO_DIRECTORY_UP + BACKEND_MODULE_NAME + SLASH + activeProfileName + DASH + BACKEND_MODULE_NAME + ENABLED);
		}
		if (pwd.contains(BACKEND_MODULE_NAME)) {
			pathsToActivationFiles.add(activeProfileName + "-" + BACKEND_MODULE_NAME + ENABLED);
			pathsToActivationFiles.add(GO_DIRECTORY_UP + FRONTEND_MODULE_NAME + SLASH + activeProfileName + DASH + FRONTEND_MODULE_NAME + ENABLED);
		}
		else {
			pathsToActivationFiles.add(FRONTEND_MODULE_NAME + SLASH + activeProfileName + DASH + FRONTEND_MODULE_NAME + ENABLED);
			pathsToActivationFiles.add(BACKEND_MODULE_NAME + SLASH + activeProfileName + DASH + BACKEND_MODULE_NAME + ENABLED);
		}
		return pathsToActivationFiles;
	}
}
