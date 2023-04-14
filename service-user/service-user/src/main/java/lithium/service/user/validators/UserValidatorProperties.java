package lithium.service.user.validators;

import java.util.regex.Pattern;

public class UserValidatorProperties {
  public final static String CHECK_NAME_PATTERN = "^[\\p{L}\\p{Pd}\\p{Po}\\p{Sk}+ ]*$";
  public final static int MIN_NAME_LENGTH = 2;
  public final static int MAX_NAME_LENGTH = 255;
  public final static String EMAIL_REGEX = "^$|[_A-Za-z0-9-\\+]+(\\.[_+A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
  public final static Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

}
