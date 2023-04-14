package lithium.service.migration.util.columns;

public enum TransactionsColumn {
  CUSTOMER_ID("customerid"),
  OLD_BALANCE("oldBalance"),
  NEW_BALANCE("newBalance"),
  TOTAL_BALANCE("totalbalance"),
  CREATION_DATE("creationDate"),
  TRANSACTION_TYPE_LITHIUM("transactionTypeLithium"),
  AMOUNT("amount"),
  GAME_ID("GameId"),
  RTP("rtp"),
  SUB_PROVIDER_ID("SubProviderID"),
  SUB_PROVIDER_NAME("SubProviderName"),
  BET_ID("betid"),
  GAME_PROVIDER_NAME("GameProviderName"),
  TURNOVER("turnover"),
  RETURN("return"),
  PLACEMENT_DATE("placementDate"),
  PLACEMENT_DATE_TIME("placementDateTime"),
  SETTLEMENT_DATE("settlementDate"),
  SETTLEMENT_DATE_TIME("settlementDateTime"),
  GAME_NAME("GameName");
  public final String TransactionColumnName;
  private TransactionsColumn (String label) {this.TransactionColumnName = label;
  }
}
