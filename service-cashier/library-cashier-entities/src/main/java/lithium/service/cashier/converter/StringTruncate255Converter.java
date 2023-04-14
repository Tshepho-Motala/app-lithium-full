package lithium.service.cashier.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

@Convert
public class StringTruncate255Converter implements AttributeConverter<String, String> {
  private static final int LIMIT = 255;

  @Override
  public String convertToDatabaseColumn(String attribute) {
    if (attribute == null) {
      return null;
    } else if (attribute.length() > LIMIT) {
      return attribute.substring(0, LIMIT);
    } else {
      return attribute;
    }
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    return dbData;
  }
}
