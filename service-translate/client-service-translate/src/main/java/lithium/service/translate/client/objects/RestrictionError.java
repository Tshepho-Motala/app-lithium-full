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
public enum RestrictionError {
  DEFAULT_ERROR_MESSAGE("User restriction triggered."),
  CASINO("User restriction triggered: Casino not allowed."),
  LOGIN("User restriction triggered: Login not allowed."),
  DEPOSIT("User restriction triggered: Deposit not allowed."),
  WITHDRAW("User restriction triggered: Withdraw not allowed."),
  BET_PLACEMENT("User restriction triggered: Bet placement not allowed."),
  F2P("User restriction triggered: F2P not allowed.");

  @JsonIgnore
  private String description;

  public String getResponseMessageLocal(SubModule subModule, MessageSource messageSource, String domainName) {
    return messageSource.getMessage(getCode(subModule), new Object[]{new Domain(domainName)}, this.description, LocaleContextHolder.getLocale());
  }

  public String getResponseMessageLocal(SubModule subModule, MessageSource messageSource, String domainName, Object[] args) {
    List<Object> objectList = new ArrayList<>();
    objectList.add(new Domain(domainName));
    objectList.addAll(Arrays.asList(args));

    return messageSource.getMessage(getCode(subModule), objectList.toArray(), this.description, LocaleContextHolder.getLocale());
  }

  public String getResponseMessageLocal(MessageSource messageSource, String domainName, String fullTranslationCode, String defaultMessage, Object[] args) {
    List<Object> objectList = new ArrayList<>();
    objectList.add(new Domain(domainName));
    objectList.addAll(Arrays.asList(args));

    return messageSource.getMessage(fullTranslationCode, objectList.toArray(), defaultMessage, LocaleContextHolder.getLocale());
  }

  public String getResponseMessageLocal(MessageSource messageSource, String domainName, String fullTranslationCode, String defaultMessage) {
    return messageSource.getMessage(fullTranslationCode, new Object[]{new Domain(domainName)}, defaultMessage, LocaleContextHolder.getLocale());
  }

  public String getResponseMessageLocal(MessageSource messageSource, String domainName, String fullTranslationCode) {
    return messageSource.getMessage(fullTranslationCode, new Object[]{new Domain(domainName)}, fullTranslationCode, LocaleContextHolder.getLocale());
  }

  public String getCode(SubModule subModule) {
    return Module.ERROR_DICTIONARY.name() + "." + subModule.name() + "." + this.name();
  }

  public String getDescription() { return this.description; }
}