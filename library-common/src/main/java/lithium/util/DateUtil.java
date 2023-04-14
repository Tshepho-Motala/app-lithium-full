package lithium.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Seconds;
import org.springframework.util.ObjectUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtil {
	public static int secondsBetween(Date first, Date second) {
		return Seconds.secondsBetween(new DateTime(first), new DateTime(second)).getSeconds();
	}

	public static long secondsBetween(LocalDateTime first, LocalDateTime second) {
		return ChronoUnit.SECONDS.between(first, second);
	}

	public static long minutesBetween(LocalDateTime first, LocalDateTime second) {
		return ChronoUnit.MINUTES.between(first, second);
	}

	public static DateTime toDateTime(LocalDateTime localDate) {
		return new DateTime(DateTimeZone.UTC)
				.withDate(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth())
				.withHourOfDay(localDate.getHour())
				.withMinuteOfHour(localDate.getMinute())
				.withSecondOfMinute(localDate.getSecond());
	}

	public static boolean isAfterNow(DateTime dateTime) {
		return dateTime.isAfterNow();
	}

	public static String getFullStringDate(Integer day, Integer month, Integer year) {
		String fullStringDate = "not set";
		if(!ObjectUtils.isEmpty(day)  && !ObjectUtils.isEmpty(month) && !ObjectUtils.isEmpty(year)) {
			fullStringDate = day+ "/" + month + "/" + year;
		}
		return fullStringDate;
	}

	public static String timestampToHumanReadable(Long timestamp) {
		if (timestamp == null) return "";

		long timeInSec = Math.round(timestamp / 1000);
		int hours = (int) Math.floor(timeInSec / 3600);
		int minutes = (int)Math.floor(timeInSec /60 - hours * 60);
		int seconds = (int)Math.floor(timeInSec - hours * 3600 - minutes*60);

		String hoursStr = Integer.toString(hours);
		String minutesStr = Integer.toString(minutes);
		String secondsStr = Integer.toString(seconds);

		if (hoursStr.length()<2) {
			hoursStr = "0" + hoursStr;
		}
		if (minutesStr.length()<2) {
			minutesStr = "0" + minutesStr;
		}
		if (secondsStr.length()<2) {
			secondsStr = "0" + secondsStr;
		}
		return hoursStr + " : " + minutesStr + " : "+ secondsStr;
	}
}
