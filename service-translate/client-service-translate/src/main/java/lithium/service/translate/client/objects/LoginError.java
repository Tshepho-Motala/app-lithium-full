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
public enum LoginError {
  LOGIN_ERROR("Error occured while trying to login in a player."),
  UNAUTHORIZED_USER("Unauthorized request"),
  EXCESSIVE_FAILED_LOGIN_BLOCK("Excessive failed logins."),
  ACCESS_DENIED("Your username or password is incorrect."),
  USER_DISABLED("Your account is disabled."),
  UNAUTHORIZED("User is unauthorised."),
  IP_BLOCKED("IP Blocked."),
  ACCOUNT_FROZEN_GAMESTOP_SELF_EXCLUDED("Account Frozen - Gamstop Self Excluded."),
  ADDITIONAL_INFORMATION("You need to provide additional information to complete registration."),
  ACCOUNT_BLOCKED("Account Blocked."),
  ACCOUNT_BLOCKED_PLAYER_REQUEST("Account Blocked - Player Request."),
  ACCOUNT_BLOCKED_RESPONSIBLE_GAMING("Account Blocked - Responsible Gaming."),
  ACCOUNT_BLOCKED_AML("Account Blocked - AML."),
  ACCOUNT_BLOCKED_DUPLICATE_ACCOUNT("Account Blocked - Duplicated Account."),
  ACCOUNT_BLOCKED_FRAUD("Account Blocked - Fraud."),
  ACCOUNT_BLOCKED_OTHER("Account Blocked - Other."),
  ACCOUNT_FROZEN("Account Frozen."),
  ACCOUNT_FROZEN_COOLING_OFF("Account Frozen - Cooling Off."),
  ACCOUNT_FROZEN_SELF_EXCLUDED("Account Frozen - Self Excluded."),
  RESTRICTED("Login Restricted."),
  DOMAIN_UNKNOWN_COUNTRY("You can't login from an unknown country. Please contact customer support."),
  SOFT_SELF_EXCLUSION("Soft self exclusion."),
  PERMANENT_SELF_EXCLUSION("Permanent self exclusion."),
  FLAGGED_AS_COOLING_OFF("Player account is flagged as cooling off."),
  INTERNAL_SERVER_ERROR("Internal server error."),
  ACCOUNT_FROZEN_CRUKS_SELF_EXCLUSION("Account Frozen - CRUKS Self Excluded."),
  INVALID_CLIENT_AUTH("Invalidated client auth."),
  USER_EXIST_IN_ECOSYSTEM_IN_OTHER_DOMAIN("The player already has an account in the ecosystem and has an account on another mutually exclusive domain. Try logging in using the correct domain name in your username combination. domainName = {0}."),
  BACKOFFICE_ORIGIN_LOGIN_BLOCK("User not allowed to log into a backoffice account using a player login, please contact your system administrator."),
  PLAYER_ORIGIN_LOGIN_BLOCK("User not allowed to log into a player account using a backoffice login, please contact your system administrator.");

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
    return Module.ERROR_DICTIONARY.name() + "." + SubModule.LOGIN.name() + "." + this.name();
  }

  public String getDescription() { return this.description; }
}
