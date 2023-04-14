package lithium.service.migration.util.columns;

public enum PlayerLimitColumn {

  //PLAYER_GUID("playerGuid"),
  CUSTOMER_ID("customerID"),
  LIMIT_TYPE("limitType"),
  GRANULARITY("granularity"),
  AMOUNT_CENTS("amountCents");

  public final String PlayerLimitColumnName;

  private PlayerLimitColumn(String label){
    this.PlayerLimitColumnName = label;
  }

}
