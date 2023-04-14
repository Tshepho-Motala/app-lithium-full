package lithium.service.migration.util.columns;

public enum CashierColumn {
  CUSTOMER_ID("customerId"),
  TRANSACTION_ID("transactionId"),
  TYPE("type"),
  STATUS("status"),
  CREATED_DATE("createdDate"),
  UPDATED_DATE("updatedDate"),
  CURRENCY_CODE("currencyCode"),
  AMOUNT("amount"),
  OPERATION_TYPE_DESCRIPTION("operationTypeDescription"),
  PAYMENT_METHOD_TYPE("paymentMethodType"),
  PAYMENT_METHOD("paymentMethod"),
  PAYMENT_METHOD_NAME("PaymentMethodName"),
  PAYMENT_PROVIDER("paymentProvider"),
  PAYMENT_PROVIDER_NAME("PaymentProviderName"),
  OPERATION_GROUP_DESCRIPTION("operationGroupDescription"),
  OPERATION_DESCRIPTION("operationDescription"),
  OPERATION_CATEGORY("operationCategory");

  public final String columnName;

  CashierColumn(String columnName) {
    this.columnName = columnName;
  }
}
