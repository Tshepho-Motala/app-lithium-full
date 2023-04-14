package lithium.service.translate.client.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ToString
@AllArgsConstructor()
public enum RegistrationError {
  REGISTRATION_ERROR("Error occured while registering a player."),
  ACCESS_DENIED("Access denied from {0} rules."),
  NO_SUCH_DOMAIN("No such domain."),
  DOMAIN_IS_DISABLED("Domain is disabled."),
  DOMAIN_NON_EXISTING("Domain does not exist."),
  NOT_PLAYER_DOMAIN("Not a player domain."),
  INVALID_PARAMETER("Invalid parameter provided."),
  INVALID_EMAIL("Invalid email provided."),
  INVALID_FIRST_NAME("Invalid first name."),
  INVALID_LAST_NAME("Invalid last name."),
  INVALID_DOB_YEAR("Invalid dob year."),
  INVALID_DOB_MONTH("Invalid dob month."),
  INVALID_DOB_DAY("Invalid dob day."),
  INVALID_REGISTRATION_DATE("Invalid registration date : {0}."),
  USERNAME_NOT_UNIQUE("The supplied username is not unique."),
  EMAIL_NOT_UNIQUE("The supplied email is not unique."),
  CELLPHONE_NOT_UNIQUE("The supplied cellphone is not unique."),
  INVALID_DOB("Invalid date of birth specified."),
  PASSWORD_HASHING("Password hashing failed."),
  INVALID_CELLPHONE("Invalid cellphone provided."),
  POST_SIGNUP_ACCESS_RULE("User registered, but post signup access rules failed."),
  INVALID_CLIENT_AUTH("Invalidated client auth."),
  USER_EXIST_IN_ECOSYSTEM_NOT_IN_DOMAIN("The user already exists within the ecosystem but not on the specific domain."),
  USER_EXIST_IN_ECOSYSTEM_IN_OTHER_DOMAIN("The user already exists within the ecosystem and has an account on another mutually exclusive domain."),
  USER_NOT_UNIQUE("FirstName + LastName + Dob not unique exception"),
  USER_UNDERAGE("User is below the legal age"),
  SERVER_OAUTH2("Invalidated client auth."),
  INVALID_HASH("Invalid SHA256 Provided."),
  PROVIDER_NOT_CONFIGURED("Provider invalid or not found."),
  PROVIDER_DISABLED("Provider not enabled."),
  ACCESS_RULE_NOT_FOUND("Access rule invalid or not found"),
  DATA_VALIDATION_ERROR("Please check your request and make sure you are providing all the required fields."),
  INTERNAL_SERVER_ERROR("Internal server error."),
  INVALID_USERNAME("Username validation failed. Required (8+ characters and only letters and numbers are allowed)"),
  INVALID_USER_UPDATE("User update not necessary."),
  PASSWORD_NOT_COMPLEX("Password validation failed. Required (8+ characters and your password may not be your username and have at least 3 of the following [uppercase letters, lowercase letters, numbers and special characters])"),
  INVALID_LASTNAME_PREFIX("Last name prefix validation failed. Only character values are allowed"),
  BALANCE_LIMIT_NOT_PROVIDED("Balance limit not provided."),
  DEPOSIT_LIMIT_NOT_PROVIDED("Deposit limit not provided."),
  TIME_LIMIT_NOT_PROVIDED("Time limit not provided."),
  CORRELATION_ID_NOT_PROVIDED("Correlation ID not provided on HTTP Header."),
  COUNTRY_NOT_PROVIDED("Country not provided."),
  TIME_SLOT_LIMIT_NOT_PROVIDED("Time frame/slot limit not provided."),
  TIME_SLOT_LIMIT_INVALID_RANGE("Time-Slot-Limit-Start & Time-Slot-Limit-End difference must be within a 24h period '00:00 - 23:59' & in 24h format HH:MM"),
  INCOMPLETE_USER_REGISTRATION("Player did not complete iDin registration for stage one.");

  @JsonIgnore
  private String description;

  public String getResponseMessageLocal(MessageSource messageSource, String domainName) {
    return messageSource.getMessage(getCode(), new Object[]{new Domain(domainName)}, this.description, LocaleContextHolder.getLocale());
  }

  public String getResponseMessageLocal(MessageSource messageSource, String domainName, Object[] args) {
    List<Object> objectList = new ArrayList<>();
    objectList.add(new Domain(domainName));
    objectList.addAll(Arrays.asList(args));

    return messageSource.getMessage(getCode(), objectList.toArray(), this.description, LocaleContextHolder.getLocale());
  }

  public String getResponseMessageLocal(MessageSource messageSource, String domainName, String fullTranslationCode, String defaultMessage) {
    return messageSource.getMessage(fullTranslationCode, new Object[]{new Domain(domainName)}, defaultMessage, LocaleContextHolder.getLocale());
  }

  public String getCode() {
    return Module.ERROR_DICTIONARY.name() + "." + SubModule.REGISTRATION.name() + "." + this.name();
  }
}
