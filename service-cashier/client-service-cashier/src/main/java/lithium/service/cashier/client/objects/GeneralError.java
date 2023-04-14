package lithium.service.cashier.client.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@ToString
@AllArgsConstructor()
public enum GeneralError {
  GENERAL_ERROR(0, "Transaction declined. Please verify your details and try again or use another method."),
  DUPLICATE_CARD(1, "You cannot deposit with these payment details, try another option or contact Customer Service"),
  TRY_AGAIN_LATER(2, "Transaction declined. Please try again later or use another method."),
  CONTACT_YOUR_BANK(3, "Transaction declined. Please contact your bank or try another payment method."),
  VERIFY_CARD_DETAILS(4, "Transaction declined. Please verify the card details and try again or use another method."),
  VERIFY_INPUT_DETAILS(5, "Transaction declined. Please check your details and try again or use another method."),
  LIMIT_EXCEEDED(6, "Transaction declined due to exeeded bank limits. Please try again later or use another method."),
  CREDIT_CARD_PROHIBITED(7, "Regulation does not permit credit card deposits. Please use a debit card."),
  FAILED_TO_ADD_ACCOUNT(8, "Sorry, bank account could not be added. Please try again later or contact Customer Service."),
  FAILED_TO_ADD_BILLING_AGREEMENT(9, "Sorry, PayPal Billing agreement could not be added. Please try again later or contact Customer Service."),
  AVS_CHECK_FAILED(10, "Transaction declined. The address on your account did not match your card information. Please try another method or contact Customer Service."),
  INSUFFICIENT_FUNDS(11, "Transaction declined. Please try different amount or another debit card."),
  ANOTHER_METHOD(12, "Transaction declined. Please try again with another method"),
  LIMIT_BALANCE_CAP(13, "Sorry, this amount will exceed your balance limit. Maximum allowed is X.XX"),
  FAILED_NAME_MISMATCH(14, "Transaction declined. The name on the payment method does not match your account details. Try again by using your own financial instruments or contact us for assistance."),
  CANCEL_ADD_ACCOUNT(15, "Adding bank account is cancelled."),
  CANCEL_TRANSACTION(16, "Transaction canceled by player."),
  INVALID_PROCESSOR_ACCOUNT(17, "Invalid processor account."),
  EXPIRED_TRANSACTION(18, "Transaction was expired."),

  REACHED_MAX_ACCOUNT_COUNT(19, "Transaction declined. You have reached the maximum number of active cards. Contact us for assistance or use another payment method.");

  private Integer code;

  @JsonIgnore
  private String description;

  @JsonValue
  public Integer getCode() {
    return code;
  }

  public String getResponseMessageLocal(MessageSource messageSource, String domainName) {
    return this.code + ": " + messageSource.getMessage("ERROR_DICTIONARY.CASHIER." + this.name(), new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, this.description, LocaleContextHolder.getLocale());
  }

  public String getResponseMessageLocal(MessageSource messageSource, String domainName, Locale locale) {
    return this.code + ": " + messageSource.getMessage("ERROR_DICTIONARY.CASHIER." + this.name(), new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, this.description, locale);
  }

  public String getResponseMessageLocal(MessageSource messageSource, String domainName, String lang) {
    return this.code + ": " + messageSource.getMessage("ERROR_DICTIONARY.CASHIER." + this.name(), new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, this.description, new Locale(lang));
  }

  public String getResponseMessage() {
    return this.code + ": " + this.name();
  }

  @JsonCreator
  public static GeneralError fromErrorCode(Integer errorCode) {
    for (GeneralError s: GeneralError.values()) {
      if (s.getCode() == errorCode) {
        return s;
      }
    }
    return GENERAL_ERROR;
  }
}
