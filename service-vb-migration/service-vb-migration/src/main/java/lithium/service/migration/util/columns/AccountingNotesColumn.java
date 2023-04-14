package lithium.service.migration.util.columns;

public enum AccountingNotesColumn {

  CUSTOMER_ID("CustomerID"),
  ID("ID"),
  SUB_CATEGORY_NAME("SubCategoryName"),
  CATEGORY_NAME("CategoryName"),
  CREATED_DATE("CreationDate"),
  DELETE_DATE("DeleteDate"),
  COMMENT("Note"),
  IS_DELETE("IsDeleted");

  public final String columnName;

  AccountingNotesColumn(String columnName){
    this.columnName = columnName;
  }

}
