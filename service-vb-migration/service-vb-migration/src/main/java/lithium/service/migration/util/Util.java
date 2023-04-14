package lithium.service.migration.util;

import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValueList;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import lithium.service.limit.client.LimitType;
import lithium.service.user.client.enums.Status;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import lithium.service.user.client.enums.StatusReason;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Util {

  public static String removeLastChar(FieldValueList fieldValues, String columnName, FieldList fields) {
    String s = getStringFieldValue(fieldValues, columnName, fields);
    return (s == null || s.length() == 0)
        ? null
        : (s.substring(0, s.length() - 1));
  }

  public static String formatDate(String nDate, String pat) {
    long lng = (long) Double.parseDouble(nDate);
    DateFormat dateFormat = new SimpleDateFormat(pat);
    return dateFormat.format(new Date(lng * 1000));
  }


  public static String removeSelfExclusion(String email) {
    String trimmedEmail = email.trim();
    if(trimmedEmail.startsWith("_trm__trm__trm_")) {
      return trimmedEmail.substring(15);
    }
    if (trimmedEmail.startsWith("_trm_")) {
      return trimmedEmail.substring(5);
    }
    if (trimmedEmail.startsWith("_se__trm_")) {
      return trimmedEmail.substring(9);
    }
    if (trimmedEmail.startsWith("_se_")) {
      return trimmedEmail.substring(4);
    }
    return trimmedEmail;
  }


  public static Status getLithiumStatus(String statusName) {
    return switch (statusName) {
      case "Terminated" -> Status.DELETED;
      case "Timeout", "Closed", "Self-exclude", "Frozen", "OnTimeOut" -> Status.FROZEN;
      default -> Status.OPEN;
    };
  }

  public static StatusReason getLithiumStatusReason(String statusName) {
    return switch (statusName) {
      case "Closed", "Self-exclude" -> StatusReason.SELF_EXCLUSION;
      case "Timeout", "Frozen", "OnTimeOut" -> StatusReason.COOLING_OFF;
      default -> null;
    };
  }

  public static int getLimitType(String limitTypeName) {
    return switch (limitTypeName) {
      case "win" -> LimitType.TYPE_WIN_LIMIT.type();
      case "loss" -> LimitType.TYPE_LOSS_LIMIT.type();
      default -> LimitType.TYPE_DEPOSIT_LIMIT.type();
    };
  }

  public static int getGranularity(String granularityName) {
    return switch (granularityName) {
      case "day" -> lithium.service.accounting.enums.Granularity.GRANULARITY_DAY.id();
      case "week" -> lithium.service.accounting.enums.Granularity.GRANULARITY_WEEK.id();
      default -> lithium.service.accounting.enums.Granularity.GRANULARITY_MONTH.id();
    };
  }

  public static Long getAmountInCentsFieldValue(FieldValueList fieldValues, String columnName, FieldList fields) {
    int index = findIndex(columnName, fields);
    if (fieldValues.get(index).isNull()) {
      return null;
    }
    return Math.round(fieldValues.get(index).getNumericValue().doubleValue() * 100);
  }

  public static short getHashingAlgorithmFieldValue(FieldValueList fieldValues, String columnName, FieldList fields) {
    int index = findIndex(columnName, fields);
    if (fieldValues.get(index).isNull()) {
      return 0;
    }
    return fieldValues.get(index).getNumericValue().shortValue();
  }

  public static boolean getBooleanFieldValue(FieldValueList fieldValues, String columnName, FieldList fields) {
    int index = findIndex(columnName, fields);
    if (fieldValues.get(index).isNull()) {
      return false;
    }
    return fieldValues.get(index).getNumericValue().doubleValue() > 0;
  }

  public static LocalDate getLocalDateFieldValue(FieldValueList fieldValues, String columnName, FieldList fields) {
    int index = findIndex(columnName, fields);

    if (fieldValues.get(index).isNull()) {
      return null;
    }
    return toLocalDate(fieldValues.get(index).getStringValue());
  }

  public static Long getTimestampFieldValue(FieldValueList fieldValues, String columnName, FieldList fields) {
    int index = findIndex(columnName, fields);
    if (fieldValues.get(index).isNull()) {
      return null;
    }
    return fieldValues.get(index).getTimestampValue() / 1000;
  }

  public static BigDecimal getBigDecimalFieldValue(FieldValueList fieldValue, String columnName, FieldList fields) {
    int index = findIndex(columnName, fields);

    if (fieldValue.get(index).isNull()) {
      return null;
    }

    return fieldValue.get(index).getNumericValue();
  }

  public static String getStringFieldValue(FieldValueList fieldValues, String columnName, FieldList fields) {
    int index = findIndex(columnName, fields);
    if (fieldValues.get(index).isNull()) {
      return null;
    }
    return fieldValues.get(index).getStringValue();
  }

  public static Long getLongFieldValue(FieldValueList fieldValues, String columnName, FieldList fields) {
    int index = findIndex(columnName, fields);
    if (fieldValues.get(index).isNull()) {
      return null;
    }
    return fieldValues.get(index).getLongValue();
  }

  public static Double getDoubleFieldValue(FieldValueList fieldValues, String columnName, FieldList fields) {
    int index = findIndex(columnName, fields);
    if (fieldValues.get(index).isNull()) {
      return null;
    }
    return fieldValues.get(index).getDoubleValue();
  }

  public static Float getFloatFieldvalue(FieldValueList fieldValues, String columnName, FieldList fields){
    int index = findIndex(columnName, fields);
    if (fieldValues.get(index).isNull()) {
      return null;
    }
    return fieldValues.get(index).getNumericValue().floatValue();
  }

  public static Date getDateFieldValue(FieldValueList fieldValues, String columnName, FieldList fields) {
    int index = findIndex(columnName, fields);
    if (fieldValues.get(index).isNull()) {
      return null;
    }
    return new Date(fieldValues.get(index).getTimestampValue() / 1000);
  }

  public static Date getUpdatedDateFieldValue(FieldValueList fieldValues, String columnName, FieldList fields) throws ParseException {
    int index = findIndex(columnName, fields);
    if (fieldValues.get(index).isNull()) {
      return null;
    }

    SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    return sdf.parse(fieldValues.get(index).getStringValue());
  }

  private static int findIndex(String columnName, FieldList fields) {
    int index = 0;
    for (Field field : fields) {
      if (field.getName().equals(columnName)) {
        return index;
      }
      index++;
    }
    return index;
  }

  public static LocalDate getDateFromTimeStampFieldValue(FieldValueList fieldValues, String columnName, FieldList fields) {
    int index = findIndex(columnName, fields);
    if (fieldValues.get(index).isNull()) {
      return null;
    }
    return LocalDate.ofInstant(Instant.ofEpochMilli(fieldValues.get(index).getTimestampValue() / 1000), ZoneId.of("UTC"));
  }

  private static LocalDate toLocalDate(String localDate) {
    return LocalDate.parse(localDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }
}
