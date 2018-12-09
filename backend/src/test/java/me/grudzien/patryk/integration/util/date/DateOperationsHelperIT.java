package me.grudzien.patryk.integration.util.date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import me.grudzien.patryk.domain.enums.ApplicationZone;
import me.grudzien.patryk.util.date.DateOperationsHelper;

import static org.assertj.core.api.Assertions.assertThat;

class DateOperationsHelperIT {

	private final DateOperationsHelper dateOperationsHelper = new DateOperationsHelper();

	private static ZonedDateTime pacificZoneDateTime;
	private static ZonedDateTime americaCuiaba;
	private ZonedDateTime applicationZoneDateTime;

	@BeforeEach
	void setUp() {
		pacificZoneDateTime = ZonedDateTime.now(ZoneId.of("Pacific/Majuro"));
		applicationZoneDateTime = ZonedDateTime.now(ZoneId.of(ApplicationZone.POLAND.getZoneId()));
		americaCuiaba = ZonedDateTime.now(ZoneId.of("America/Cuiaba"));
	}

	@Test
	void periodDurationDifferenceBetween2Locations() {
		// when
		final long periodDifference = dateOperationsHelper.getPeriodDifferenceBetween(applicationZoneDateTime, pacificZoneDateTime, ChronoUnit.YEARS);
		final long durationDifference = dateOperationsHelper.getDurationDifferenceBetween(applicationZoneDateTime, pacificZoneDateTime, ChronoUnit.SECONDS);

		// then
		Assertions.assertAll(
				() -> assertThat(periodDifference).isEqualTo(0L),
				() -> assertThat(durationDifference).isGreaterThan(0L)
		);
	}

	@Test
	void getTimeOffset() {
		// when
		final Float polandFloatOffset = dateOperationsHelper.getTimeOffset(applicationZoneDateTime, Float.class);
		final Double polandDoubleOffset = dateOperationsHelper.getTimeOffset(applicationZoneDateTime, Double.class);

		final Float americaCuiabaFloatOffset = dateOperationsHelper.getTimeOffset(americaCuiaba, Float.class);
		final Double americaCuiabaDoubleOffset = dateOperationsHelper.getTimeOffset(americaCuiaba, Double.class);

        final Integer polandIntegerOffset = dateOperationsHelper.getTimeOffset(applicationZoneDateTime, Integer.class);

        // then
        Assertions.assertAll(
                () -> assertThat(polandFloatOffset).isEqualTo(1.0F),
                () -> assertThat(polandDoubleOffset).isEqualTo(1.0),
                () -> assertThat(americaCuiabaFloatOffset).isEqualTo(-3.0F),
                () -> assertThat(americaCuiabaDoubleOffset).isEqualTo(-3.0),
                () -> assertThat(polandIntegerOffset).isNull()
        );
    }

	@Test
	void adjustTimeToApplicationZone() {
		// when
		final LocalDateTime adjustedTimeForAmerica = dateOperationsHelper.adjustTimeToApplicationZone(americaCuiaba);
		final LocalDateTime adjustedTimeForPacific = dateOperationsHelper.adjustTimeToApplicationZone(pacificZoneDateTime);

		// then
		Assertions.assertAll(
                () -> assertThat(adjustedTimeForAmerica.toLocalDate().getDayOfWeek()).isEqualTo(applicationZoneDateTime.toLocalDate().getDayOfWeek()),
                () -> assertThat(adjustedTimeForAmerica.toLocalTime().getHour()).isEqualTo(applicationZoneDateTime.toLocalTime().getHour()),
                () -> assertThat(adjustedTimeForAmerica.toLocalTime().getMinute()).isEqualTo(applicationZoneDateTime.toLocalTime().getMinute()),
                () -> assertThat(adjustedTimeForPacific.toLocalDate().getDayOfWeek()).isEqualTo(applicationZoneDateTime.toLocalDate().getDayOfWeek()),
                () -> assertThat(adjustedTimeForPacific.toLocalTime().getHour()).isEqualTo(applicationZoneDateTime.toLocalTime().getHour()),
                () -> assertThat(adjustedTimeForPacific.toLocalTime().getMinute()).isEqualTo(applicationZoneDateTime.toLocalTime().getMinute())
		);
	}
}