package me.grudzien.patryk.utils.appplication;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public enum ApplicationZone {

	POLAND {
		@Override
		public String getZoneId() {
			return ZoneId.getAvailableZoneIds()
			             .stream()
			             .filter(zoneId -> zoneId.equals("Poland"))
			             .findFirst()
			             .orElseThrow(() -> new RuntimeException("Cannot find \"Poland\" zoneId!"));
		}

        @Override
        public ZonedDateTime now() {
            return ZonedDateTime.now(ZoneId.of(getZoneId()));
        }
    };

	public abstract String getZoneId();

	public abstract ZonedDateTime now();
}
