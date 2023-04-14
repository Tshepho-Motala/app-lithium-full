package lithium.service.migration.util.columns;

public enum Columns {
  USERNAME("UserName"),
  FIRST_NAME("FirstName"),
  TITLE("Title"),
  LAST_NAME("LastName"),
  USERID("CustomerID"),
  EMAIL("Email"),
  GENDER_1("Gender_1"),
  CODE("Code"),
  MOBILE("mobile"),
  ADDRESS_LINE("AddressLine"),
  COUNTRY_NAME("CountryName"),
  ZIP_CODE("ZipCode"),
  CITY_1("City_1"),
  B_TAG("bTag"),
  OPT_IN("OptIn"),
  EMAIL_OPT_OUT("MarketingpreferencesPromotionalEmails"),
  POST_OPT_OUT("MarketingpreferencesSnailMails"),
  SMS_OPT_OUT("MarketingpreferencesSMS"),
  CALL_OPT_OUT("MarketingpreferencesPhone"),
  EMAIL_VALIDATED("EmailIsVerified"),
  STATUS_NAME("StatusName"),
  CELLPHONE_VALIDATED("IsCellularPhoneValid"),
  AGE_VERIFIED("ISAgeVerified"),
  DATE_OF_BIRTH_1("DateOfBirth_1"),
  REG_DATE("Reg_Date"),
  SECURITY_QUESTION("SecurityQuestion"),
  SECURITY_ANSWER("SecurityAnswer"),
  SALT("Salt"),
  HASHED_PASSWORD("HashedPassword"),
  ADDRESS_VERIFIED("IsAddressVerified"),
  IS_EXCEPTION_CUSTOMER("IsExceptionCustomer"),
  SELF_EXCLUSION_DATE("SelfExclutionDate"),
  SELF_EXCLUSION_PERIOD("SelfExclutionPeriod"),
  PASSWORD_HASHING_ALGORITHM("PasswordHashingAlgorithm");

  public final String columnName;

  private Columns(String label) {
    this.columnName = label;
  }

}
