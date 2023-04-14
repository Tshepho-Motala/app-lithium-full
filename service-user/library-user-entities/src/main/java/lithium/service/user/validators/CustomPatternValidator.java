package lithium.service.user.validators;

import lithium.service.user.data.entities.Domain;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class CustomPatternValidator implements ConstraintValidator<CustomPattern, Object> {

  private String emailIdentifier;
  private String firstNameIdentifier;
  private String lastNameIdentifier;
  private String telephoneNumberIdentifier;
  private String cellphoneNumberIdentifier;

  private String domainIdentifier;

  public CustomPatternValidator() {}

  @Override
  public void initialize(CustomPattern parameters) {
    emailIdentifier = parameters.email();
    firstNameIdentifier = parameters.firstName();
    lastNameIdentifier = parameters.lastName();
    telephoneNumberIdentifier = parameters.telephoneNumber();
    cellphoneNumberIdentifier = parameters.cellphoneNumber();
    domainIdentifier = parameters.domain();
  }
  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    try
    {
      Domain domainValue = (Domain) PropertyUtils.getProperty(value, domainIdentifier);

      if(domainValue.getIsTestDomain()) {
        return true;
      } else {
        return (
            validatePattern(getProperty(value, firstNameIdentifier), "^[\\p{L}\\p{Pd}\\p{Po}\\p{Sk}+ ]*$") &&
            validatePattern(getProperty(value, emailIdentifier), "^$|[_A-Za-z0-9-\\+]+(\\.[_+A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$") &&
            validatePattern(getProperty(value, lastNameIdentifier), "^[\\p{L}\\p{Pd}\\p{Po}\\p{Sk}+ ]*$") &&
            validatePattern(getProperty(value, telephoneNumberIdentifier), "^((\\+[0-9]{1,3}){0,1}[0-9\\]{4,14}(?:x.+)?){0,1}$") &&
            validatePattern(getProperty(value, cellphoneNumberIdentifier), "^((\\+[0-9]{1,3}){0,1}[0-9\\]{4,14}(?:x.+)?){0,1}$")
        );
      }
    }
    catch (final Exception ignore)
    {
      throw new RuntimeException("The validator has failed");
    }
  }

  public String getProperty(Object value, String identifier) {
    try {
      return BeanUtils.getProperty(value, identifier);
    } catch(Exception ex) {
      return null;
    }
  }
  
  public Boolean validatePattern(String value, String regex) {
    if(value == null) return true;

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(value);

    return matcher.find();
  }
}


