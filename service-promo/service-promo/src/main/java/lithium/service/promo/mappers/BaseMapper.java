package lithium.service.promo.mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class BaseMapper {

    protected final  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public String formatDate(Date date) {

        if(date == null) {
            return "";
        }

        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"));
        return zonedDateTime.format(formatter);
    }

    public String formatDate(LocalDateTime localDateTime) {

        if(localDateTime == null) {
            return "";
        }

        ZonedDateTime zonedDateTim = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));
        return zonedDateTim.format(formatter);
    }
}
