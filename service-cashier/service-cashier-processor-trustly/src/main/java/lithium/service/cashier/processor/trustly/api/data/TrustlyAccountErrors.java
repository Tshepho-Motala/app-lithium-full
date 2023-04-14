package lithium.service.cashier.processor.trustly.api.data;

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
public enum TrustlyAccountErrors {
  ERROR_FUNCTION_ACCESS_DENIED(602, GeneralError.FAILED_TO_ADD_ACCOUNT, "The merchant does not have access to this function."),
  ERROR_HOST_ACCESS_DENIED(602, GeneralError.FAILED_TO_ADD_ACCOUNT, "The IP address of the merchant has not been added to Trustly's IP-whitelist."),
  ERROR_INVALID_CREDENTIALS(616, GeneralError.FAILED_TO_ADD_ACCOUNT, "The username and/or password used in the API call is incorrect."),
  ERROR_UNKNOWN(620, GeneralError.FAILED_TO_ADD_ACCOUNT, "There could be several reasons for this error, please reach out to your Trustly contact for details."),
  ERROR_INVALID_PARAMETERS(623, GeneralError.FAILED_TO_ADD_ACCOUNT, "Some value or parameter in the deposit call does not match the expected format."),
  ERROR_UNABLE_TO_VERIFY_RSA_SIGNATURE(636, GeneralError.FAILED_TO_ADD_ACCOUNT, "The signature could not be verified using the merchant's public key. Either the wrong private key was used to generate the signature, or the the data object used to create the signature was serialized incorrectly."),
  ERROR_DUPLICATE_MESSAGE_ID(637, GeneralError.FAILED_TO_ADD_ACCOUNT, "The MessageID has been used before."),
  ERROR_ENDUSER_IS_BLOCKED(638, GeneralError.FAILED_TO_ADD_ACCOUNT, "The enduser that initiated the payment is blocked."),
  ERROR_NO_PUBLIC_KEY(639, GeneralError.FAILED_TO_ADD_ACCOUNT, "No public key has been configured for the merchant on Trustly's side."),
  ERROR_INVALID_EMAIL(642, GeneralError.FAILED_TO_ADD_ACCOUNT, "The email attribute is missing or invalid (this is a requirement when using Trustly Direct Debit)."),
  ERROR_INVALID_LOCALE(645, GeneralError.FAILED_TO_ADD_ACCOUNT, "The Locale-attribute is sent with an incorrect value."),
  ERROR_DUPLICATE_UUID(688, GeneralError.FAILED_TO_ADD_ACCOUNT, "This UUID has been used before."),
  ERROR_ENDUSERID_IS_NULL(696, GeneralError.FAILED_TO_ADD_ACCOUNT, "The EndUserID sent in the request is null."),
  ERROR_MESSAGEID_IS_NULL(697, GeneralError.FAILED_TO_ADD_ACCOUNT, "The MessageID sent in the request is null"),
  ERROR_INVALID_IP(698, GeneralError.FAILED_TO_ADD_ACCOUNT, "The IP attribute sent is invalid. Only one IP address can be sent."),
  ERROR_MALFORMED_SUCCESSURL(700, GeneralError.FAILED_TO_ADD_ACCOUNT, "The SuccessURL sent in the request is malformed. It must be a valid http(s) address."),
  ERROR_MALFORMED_FAILURL(701, GeneralError.FAILED_TO_ADD_ACCOUNT, "The FailURL sent in the request is malformed. It must be a valid http(s) address."),
  ERROR_MALFORMED_TEMPLATEURL(702, GeneralError.FAILED_TO_ADD_ACCOUNT, "The TemplateURL sent in the request is malformed. It must be a valid http(s) address."),
  ERROR_MALFORMED_URLTARGET(703, GeneralError.FAILED_TO_ADD_ACCOUNT, "The URLTarget sent in the request is malformed."),
  ERROR_MALFORMED_MESSAGEID(704, GeneralError.FAILED_TO_ADD_ACCOUNT, "The MessageID sent in the request is malformed."),
  ERROR_MALFORMED_NOTIFICATIONURL(705, GeneralError.FAILED_TO_ADD_ACCOUNT, "The NotificationURL sent in the request is malformed. It must be a valid https address."),
  ERROR_MALFORMED_ENDUSERID(706, GeneralError.FAILED_TO_ADD_ACCOUNT, "The EndUserID sent in the request is malformed."),
  ERROR_DIRECT_DEBIT_NOT_ALLOWED(712, GeneralError.FAILED_TO_ADD_ACCOUNT, "Trustly Direct Debit (TDD) is not enabled on the merchant's user in Trusty's system. If you want to use TDD, please reach out to your Trustly contact. If you don't want to use TDD and still get this error message, you may need to remove the following attributes from the Deposit data: RequestDirectDebitMandate, QuickDeposit, ChargeAccountID."),
  ERROR_INVALID_ORDER_ATTRIBUTE(717, GeneralError.FAILED_TO_ADD_ACCOUNT, "One or more attributes are sent with the incorrect value. Please reach out to your Trustly contact for more information."),
  ERROR_DISABLED_USER(718, GeneralError.FAILED_TO_ADD_ACCOUNT, "The merchant's user is disabled in Trustly's system."),
  ERROR_PAY_AND_PLAY_NOT_ALLOWED(732, GeneralError.FAILED_TO_ADD_ACCOUNT, "Trustly's Pay N Play product is not enabled on the merchant's user in Trustly's system. If you want to use Pay N Play, please reach out to your Trustly contact."),
  ERROR_NOT_SECURE_NOTIFICATIONURL(734, GeneralError.FAILED_TO_ADD_ACCOUNT, "The NotificationURL must be using HTTPS, not plain HTTP."),
  ERROR_INVALID_COUNTRY(737, GeneralError.FAILED_TO_ADD_ACCOUNT, "The Country code sent in the Deposit data is invalid. The expected format is explained on the Deposit documentation page.");

  @Getter
  @Accessors(fluent = true)
  private Integer code;
  @Getter
  @Accessors(fluent = true)
  private GeneralError generalError;
  @Getter
  @Accessors(fluent = true)
  private String description;

  @JsonValue
  public Integer getCode() {
    return code;
  }

  public String getResponseMessage() {
    return this.generalError.getResponseMessage();
  }

  public String getResponseMessageLocal(MessageSource messageSource, String domainName, String lang) {
    return this.generalError.getResponseMessageLocal(messageSource, domainName, lang);
  }

  @JsonCreator
  public static TrustlyAccountErrors fromErrorCode(Integer errorCode) {
    for (TrustlyAccountErrors s: TrustlyAccountErrors.values()) {
      if (s.getCode() == errorCode) {
        return s;
      }
    }
    return ERROR_UNKNOWN;
  }
}
