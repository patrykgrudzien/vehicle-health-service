package me.grudzien.patryk.util.date;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalUnit;
import java.util.function.Function;

import me.grudzien.patryk.domain.enums.ApplicationZone;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;

public final class DateOperationsHelper {

	private static final Function<Number, String> NUMBER_AS_STRING = String::valueOf;
	private static final Function<String, Number> INDEX_OF_DOT_SEPARATOR = s -> s.indexOf(".");

	public long getPeriodDifferenceBetween(final ZonedDateTime zoneDateTime1, final ZonedDateTime zoneDateTime2, final TemporalUnit temporalUnit) {
		return Period.between(zoneDateTime1.toLocalDate(), zoneDateTime2.toLocalDate())
		             .get(temporalUnit);
	}

	public long getDurationDifferenceBetween(final ZonedDateTime zoneDateTime1, final ZonedDateTime zoneDateTime2, final TemporalUnit temporalUnit) {
		return Duration.between(zoneDateTime1.toLocalTime(), zoneDateTime2.toLocalTime())
		               .abs()
		               .get(temporalUnit);
	}

	@SuppressWarnings("unchecked")
    public <T extends Number> T getTimeOffset(final ZonedDateTime zoneDateTime, final Class<T> returnType) {
		final String offsetSign = zoneDateTime.getOffset()
		                                      .getId()
		                                      .substring(0, 1);
        final String offsetAsNumber = zoneDateTime.getOffset()
                                                  .getId()
                                                  .substring(1)
                                                  .replace(":", ".");
        return (T) Match(returnType).of(
                Case($(this::isFloatType), () -> {
                    final Float offsetFloat = Float.valueOf(offsetAsNumber);
                    return offsetSign.equalsIgnoreCase("+") ? offsetFloat * 1 : offsetFloat * -1;
                }),
                Case($(this::isDoubleType), () -> {
                    final Double offsetDouble = Double.valueOf(offsetAsNumber);
                    return offsetSign.equalsIgnoreCase("+") ? offsetDouble * 1 : offsetDouble * -1;
                }),
                // if any other exception
                Case($(), input -> run(() -> new RuntimeException("Cannot convert time offset to NOT decimal number!")))
        );
	}

	private <T> boolean isFloatType(final Class<T> type) {
	    return type.isAssignableFrom(Float.class);
    }

    private <T> boolean isDoubleType(final Class<T> type) {
	    return type.isAssignableFrom(Double.class);
    }

	public LocalDateTime adjustTimeToApplicationZone(final ZonedDateTime zoneDateTimeToAdjust) {
		final ZonedDateTime applicationZonedDateTime = ApplicationZone.POLAND.getApplicationZonedDateTimeNow();

		final Double applicationTimeOffset = getTimeOffset(applicationZonedDateTime, Double.class);
		final Double timeOffsetToAdjust = getTimeOffset(zoneDateTimeToAdjust, Double.class);

		final int minutesDifference = getMinutesDifference(applicationTimeOffset, timeOffsetToAdjust);
		final int hoursDifference = getHoursDifference(applicationTimeOffset, timeOffsetToAdjust);
		final int daysDifference = getDaysDifference(applicationZonedDateTime, zoneDateTimeToAdjust);

		return getCalculatedLocalDateTime(zoneDateTimeToAdjust, applicationTimeOffset, timeOffsetToAdjust, minutesDifference, hoursDifference, daysDifference);
    }

	private LocalDateTime getCalculatedLocalDateTime(final ZonedDateTime zoneDateTimeToAdjust, final Double applicationTimeOffset, final Double timeOffsetToAdjust,
	                                                 final int minutesDifference, final int hoursDifference, final int daysDifference) {
		return timeOffsetToAdjust < applicationTimeOffset ?
                LocalDateTime.of(zoneDateTimeToAdjust.toLocalDate().plusDays(Math.abs(daysDifference)),
                                 zoneDateTimeToAdjust.toLocalTime().plusHours(Math.abs(hoursDifference)).plusMinutes(Math.abs(minutesDifference))) :
                LocalDateTime.of(zoneDateTimeToAdjust.toLocalDate().minusDays(Math.abs(daysDifference)),
                                 zoneDateTimeToAdjust.toLocalTime().minusHours(Math.abs(hoursDifference)).minusMinutes(Math.abs(minutesDifference)));
	}

	public int getDaysDifference(final ZonedDateTime zoneDateTime1, final ZonedDateTime zoneDateTime2) {
		int daysDifference = 0;
		final Double timeOffset1 = getTimeOffset(zoneDateTime1, Double.class);
		final Double timeOffset2 = getTimeOffset(zoneDateTime2, Double.class);
		if (getHoursDifference(timeOffset1, timeOffset2) >= 12 ||
		    zoneDateTime1.toLocalDate().getDayOfMonth() != zoneDateTime2.toLocalDate().getDayOfMonth()) {
		    daysDifference++;
		}
		return daysDifference;
	}

	public int getHoursDifference(final Double applicationTimeOffset, final Double timeOffsetToAdjust) {
		final int appOffsetHours = getHoursFromOffset(applicationTimeOffset);
		final int hours = getHoursFromOffset(timeOffsetToAdjust);
		return Math.abs(appOffsetHours - hours);
	}

	public int getMinutesDifference(final Double applicationTimeOffset, final Double timeOffsetToAdjust) {
		final int appOffsetMinutes = getMinutesFromOffset(applicationTimeOffset);
		final int minutes = getMinutesFromOffset(timeOffsetToAdjust);
		return Math.abs(appOffsetMinutes - minutes);
	}

	public int getHoursFromOffset(final Double decimalNumber) {
        final String numberAsString = NUMBER_AS_STRING.apply(decimalNumber);
        final Integer indexOfDotSeparator = (Integer) INDEX_OF_DOT_SEPARATOR.apply(numberAsString);

        return Integer.valueOf(numberAsString.substring(0, indexOfDotSeparator));
    }

    public int getMinutesFromOffset(final Double decimalNumber) {
        final String numberAsString = NUMBER_AS_STRING.apply(decimalNumber);
        final Integer indexOfDotSeparator = (Integer) INDEX_OF_DOT_SEPARATOR.apply(numberAsString);

        return Integer.valueOf(numberAsString.substring(indexOfDotSeparator + 1));
    }
}
