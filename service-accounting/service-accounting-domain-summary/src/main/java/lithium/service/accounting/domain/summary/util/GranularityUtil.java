package lithium.service.accounting.domain.summary.util;

import lithium.service.client.objects.Granularity;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GranularityUtil {
	public static final String DATES_START = "start";
	public static final String DATES_END = "end";

	public static Map<String, List<DateTime>> getDatesForLastX(Granularity granularity, boolean allowTotal, int x) {
		Map<String, List<DateTime>> dates = new LinkedHashMap<>();
		List<DateTime> start = new ArrayList<>();
		List<DateTime> end = new ArrayList<>();

		DateTime now = new DateTime();

		switch (granularity) {
			case GRANULARITY_YEAR: {
				DateTime date = new DateTime(now.getYear(), 1, 1, 0, 0);
				start.add(date);
				end.add(date.plusYears(1));
				for (int i = 0; i < x; i++) {
					start.add(date.minusYears(i + 1));
					end.add(date.minusYears(i + 1).plusYears(1));
				}
				break;
			}
			case GRANULARITY_MONTH: {
				DateTime date = new DateTime(now.getYear(), now.getMonthOfYear(), 1, 0, 0);
				start.add(date);
				end.add(date.plusMonths(1));
				for (int i = 0; i < x; i++) {
					start.add(date.minusMonths(i + 1));
					end.add(date.minusMonths(i + 1).plusMonths(1));
				}
				break;
			}
			case GRANULARITY_DAY: {
				DateTime date = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0);
				start.add(date);
				end.add(date.plusDays(1));
				for (int i = 0; i < x; i++) {
					start.add(date.minusDays(i + 1));
					end.add(date.minusDays(i + 1).plusDays(1));
				}
				break;
			}
			case GRANULARITY_WEEK: {
				DateTime date = new DateTime(now.getYear(), 1, 1, 0, 0)
						.withWeekyear(now.getWeekyear())
						.withWeekOfWeekyear(now.getWeekOfWeekyear()).withDayOfWeek(1);
				start.add(date);
				end.add(date.plusWeeks(1));
				for (int i = 0; i < x; i++) {
					start.add(date.minusWeeks(i + 1));
					end.add(date.minusWeeks(i + 1).plusWeeks(1));
				}
				break;
			}
			case GRANULARITY_TOTAL: {
				if (allowTotal) {
					DateTime date = new DateTime(2015, 1, 1, 0, 0);
					start.add(date);
					end.add(now);
					break;
				} else {
					throw new RuntimeException("Invalid granularity: " + granularity);
				}
			}
			default:
				throw new RuntimeException("Invalid granularity: " + granularity);
		}

		dates.put(DATES_START, start);
		dates.put(DATES_END, end);

		return dates;
	}
}
