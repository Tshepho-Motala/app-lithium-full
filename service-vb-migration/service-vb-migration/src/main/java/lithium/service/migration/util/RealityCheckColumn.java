package lithium.service.migration.util;

public enum RealityCheckColumn {

  CUSTOMER_ID("CustomerID"),
  REALITY_CHECK_INTERVAL("RealityCheckInterval");



  public final String RealityCheckColumnName;

  private RealityCheckColumn(String label){
    this.RealityCheckColumnName = label;
  }
}
