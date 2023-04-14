package lithium.service.cashier.client.objects.enums;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DeclineReasonErrorType {

  CASHIER_PAYMENT_METHOD_UNAVAILABLE(501, "Payment method not available, please try again later."),
  CASHIER_INSUFFICIENT_BALANCE(502, "Insufficient balance"),
  CASHIER_API_TRANSACTION_DECLINED(503, "API Transaction declined"),
  CASHIER_APPROVE_ERROR(504, "Transaction approval error"),
  CASHIER_INVALID_TRANSACTION_AMOUNT(505, "Invalid transaction amount"),
  CASHIER_PROCESSOR_CALLBACK_DECLINED(506, "Processor Callback declined"),
  CASHIER_NOT_ACCEPTABLE_AMOUNT_DIFFERENCE(507, "Amount difference is not acceptable"),
  CASHIER_ACCESS_RULE_CHECK_FAILED(508, "Access rule check failed. Transaction declined."),
  CASHIER_ACCESS_RESTRICTED(509, "Access restricted. Transaction declined by system."),
  CASHIER_DEPOSIT_LIMIT_CHECK_FAILED(510, "Deposit limit check failed. Transaction declined."),
  CASHIER_PROCESSOR_CALL_DECLINED(511, "Processor Call declined"),
  CASHIER_PROCESS_STATE_DECLINED(512, "Process State declined"),
  CASHIER_DUPLICATE_ACCOUNT_DECLINED(513, "Duplicate card check failed."),
  CASHIER_PLAYTIME_LIMIT_CHECK_FAILED(514, "PlayTime limit check failed. Transaction declined."),
  CASHIER_INVALID_ACCOUNT(515, "Invalid account"),
  FAILED_TO_CONNECT_TO_PROVIDER(516, "Exception while connecting provider."),
  CASHIER_MAX_ACCOUNT_COUNT(517, "Maximum number of active accounts is reached."),

  UNKNOWN_ERROR(404, "Validation error");

  @Getter
  @Accessors(fluent = true)
  private final int code;
  @Getter
  @Accessors(fluent = true)
  private final String description;

  public static String getError(DeclineReasonErrorType errorType) {
      return errorType.code + ": " + errorType.description;
  }

  public static String getInvalidTransactionAmountMessage(BigDecimal amountEntered, BigDecimal amountReceived) {
    return getError(CASHIER_INVALID_TRANSACTION_AMOUNT) + ". Expected " + amountEntered + " but received " + amountReceived;
  }

  public static String getDepositCheckLimitMessage(String exMessage) {
    return getError(CASHIER_DEPOSIT_LIMIT_CHECK_FAILED) + ". " + exMessage;
  }

  public static String getPlayTimeCheckLimitMessage(String exMessage) {
    return getError(CASHIER_PLAYTIME_LIMIT_CHECK_FAILED) + ". " + exMessage;
  }
}
