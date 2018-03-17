package me.grudzien.patryk.utils;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Class which stores different Markers (no need to extends it).
 */
public final class LogMarkers {

	// disabling class object creation
	private LogMarkers() {}

	public static final Marker FLOW_MARKER = MarkerManager.getMarker("FLOW");
	public static final Marker CONTROLLER_MARKER = MarkerManager.getMarker("CONTROLLER");
	public static final Marker METHOD_INVOCATION_MARKER = MarkerManager.getMarker("METHOD_INVOCATION");
	public static final Marker EXCEPTION_MARKER = MarkerManager.getMarker("EXCEPTION");
}
