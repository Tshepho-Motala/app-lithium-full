package lithium.service.user.client.validators.safetext;

import lithium.util.StringUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class SafeTextValidator implements ConstraintValidator<SafeTextConstraint, String> {
  // < > & = " and '
  //PLAT-3771 required the removal of the single quote
  final String VALID_CHARS_PATTERN="^[^\\+<>&=\"\\[\\]\\^\\$]+$";
  @Override
  public void initialize(SafeTextConstraint safeTextConstraint) {}

  @Override
  public boolean isValid(String text, ConstraintValidatorContext constraintValidatorContext) {

    if(StringUtil.isEmpty(text)) {
      return true;
    }
    return Pattern.matches(VALID_CHARS_PATTERN, text);
  }
}

