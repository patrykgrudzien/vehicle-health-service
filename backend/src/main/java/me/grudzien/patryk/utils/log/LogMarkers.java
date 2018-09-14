package me.grudzien.patryk.utils.log;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Class which stores different Markers (no need to extends it).
 */
public final class LogMarkers {

	// disabling class object creation
	private LogMarkers() {
		throw new UnsupportedOperationException("Creating object of this class is not allowed!");
	}

	public static final Marker FLOW_MARKER = MarkerManager.getMarker("FLOW");
	public static final Marker CONTROLLER_MARKER = MarkerManager.getMarker("CONTROLLER");
	public static final Marker METHOD_INVOCATION_MARKER = MarkerManager.getMarker("METHOD_INVOCATION");
	public static final Marker EXCEPTION_MARKER = MarkerManager.getMarker("EXCEPTION");
	public static final Marker ASPECT_MARKER = MarkerManager.getMarker("ASPECT");
	public static final Marker SECURITY_MARKER = MarkerManager.getMarker("SECURITY");
	public static final Marker OAUTH2_MARKER = MarkerManager.getMarker("OAUTH2");
}
