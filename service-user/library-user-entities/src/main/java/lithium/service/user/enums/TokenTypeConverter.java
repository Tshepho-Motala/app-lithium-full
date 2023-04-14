package lithium.service.user.enums;

import java.beans.PropertyEditorSupport;

public class TokenTypeConverter extends PropertyEditorSupport {

  public void setAsText(final String text) throws IllegalArgumentException {
    setValue(TokenType.fromType(text));
  }
}
