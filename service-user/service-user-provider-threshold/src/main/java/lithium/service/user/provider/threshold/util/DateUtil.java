package lithium.service.user.provider.threshold.util;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

  private DateUtil() {
  }

  public static int getDiffYears(Date first, Date last) {
    Calendar a = getCalendar(first);
    Calendar b = getCalendar(last);
    int diff = b.get(YEAR) - a.get(YEAR);
    if (a.get(MONTH) > b.get(MONTH) ||
        (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
      diff--;
    }
    return diff;
  }

  public static Calendar getCalendar(Date date) {
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.setTime(date);
    return cal;
  }


  public static Date getDay(int year, int month, int day){
    Calendar c = Calendar.getInstance();
    c.set(year, month-1, day, 0, 0);
    return c.getTime();
  }

}
