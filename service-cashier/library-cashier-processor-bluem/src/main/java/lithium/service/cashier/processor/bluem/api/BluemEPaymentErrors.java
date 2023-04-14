package lithium.service.cashier.processor.bluem.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lithium.service.cashier.client.objects.GeneralError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.context.MessageSource;

@ToString
@AllArgsConstructor()
public enum BluemEPaymentErrors {
  //TODO: add real code here
  ERROR_UNKNOWN("620", GeneralError.VERIFY_INPUT_DETAILS, "There could be several reasons for this error, please reach out to your Bluem contact for details.");

  @Getter
  @Accessors(fluent = true)
  private String code;
  @Getter
  @Accessors(fluent = true)
  private GeneralError generalError;
  @Getter
  @Accessors(fluent = true)
  private String description;

  @JsonValue
  public String getCode() {
    return code;
  }

  public GeneralError getGeneralError() {
    return this.generalError;
  }

  public String getResponseMessageLocal(MessageSource messageSource, String domainName, String lang) {
    return this.generalError.getResponseMessageLocal(messageSource, domainName, lang);
  }

  @JsonCreator
  public static BluemEPaymentErrors fromErrorCode(String errorCode) {
    for (BluemEPaymentErrors s: BluemEPaymentErrors.values()) {
      if (s.getCode().equals(errorCode)) {
        return s;
      }
    }
    return ERROR_UNKNOWN;
  }
}
