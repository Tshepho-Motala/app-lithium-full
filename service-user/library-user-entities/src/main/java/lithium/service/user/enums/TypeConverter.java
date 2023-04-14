package lithium.service.user.enums;

import java.beans.PropertyEditorSupport;

public class TypeConverter extends PropertyEditorSupport {

  public void setAsText(final String text) throws IllegalArgumentException {
    setValue(Type.fromType(text));
  }
}
