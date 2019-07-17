package me.grudzien.patryk.integration.util.date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import me.grudzien.patryk.utils.app.ApplicationZone;
import me.grudzien.patryk.utils.date.DateOperationsHelper;

import static org.assertj.core.api.Assertions.assertThat;

class DateOperationsHelperIT {

	private final DateOperationsHelper dateOperationsHelper = new DateOperationsHelper();

	private static ZonedDateTime pacificZoneDateTime;
	private static ZonedDateTime americaCuiaba;
	private ZonedDateTime applicationZoneDateTime;

	@BeforeEach
	void setUp() {
		pacificZoneDateTime = ZonedDateTime.now(ZoneId.of("Pacific/Majuro"));
		americaCuiaba = ZonedDateTime.now(ZoneId.of("America/Cuiaba"));
		applicationZoneDateTime = ApplicationZone.POLAND.now();
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
		final LocalDateTime adjustedTimeForAmerica = dateOperationsHelper.adjustTimeToApplicationZoneOffset(americaCuiaba);
		final LocalDateTime adjustedTimeForPacific = dateOperationsHelper.adjustTimeToApplicationZoneOffset(pacificZoneDateTime);

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

	@Test
	void getDaysDifference() {
		// when
		final int daysDifference = dateOperationsHelper.getDaysDifference(applicationZoneDateTime, applicationZoneDateTime);

		// then
		assertThat(daysDifference).isEqualTo(0);
	}

	@Test
	void getHoursDifference() {
		// given
		final Double applicationTimeOffset = dateOperationsHelper.getTimeOffset(applicationZoneDateTime, Double.class);
		final Double pacificTimeOffset = dateOperationsHelper.getTimeOffset(pacificZoneDateTime, Double.class);

		// when
		final int hoursDifference = dateOperationsHelper.getHoursDifferenceBetweenOffsets(applicationTimeOffset, pacificTimeOffset);

		// then
		assertThat(hoursDifference).isEqualTo(11);
	}

	@Test
	void getMinutesDifference() {
		// given
		final Double applicationTimeOffset = dateOperationsHelper.getTimeOffset(applicationZoneDateTime, Double.class);
		final Double pacificTimeOffset = dateOperationsHelper.getTimeOffset(pacificZoneDateTime, Double.class);

		// when
		final int minutesDifference1 = dateOperationsHelper.getMinutesDifferenceBetweenOffsets(applicationTimeOffset, pacificTimeOffset);
		final int minutesDifference2 = dateOperationsHelper.getMinutesDifferenceBetween(applicationZoneDateTime, pacificZoneDateTime);

		// then
		assertThat(minutesDifference1).isEqualTo(0);
		assertThat(minutesDifference2).isEqualTo(0);
	}

	@Test
	void getHoursFromOffset() {
		// when
		final int hoursFromOffset = dateOperationsHelper.getHoursPartFromOffset(dateOperationsHelper.getTimeOffset(applicationZoneDateTime, Double.class));

		// then
		assertThat(hoursFromOffset).isEqualTo(1);
	}

	@Test
	void getMinutesFromOffset() {
		// when
		final int minutesFromOffset = dateOperationsHelper.getMinutesPartFromOffset(dateOperationsHelper.getTimeOffset(applicationZoneDateTime, Double.class));

		// then
		assertThat(minutesFromOffset).isEqualTo(0);
	}
}