package lithium.service.cashier.client.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

@ToString
@AllArgsConstructor()
public enum ProcessorAccountVerificationType {
  MANUAL_VERIFICATION("MANUAL_VERIFICATION", Arrays.asList(ProcessorAccountType.values()), "Manually verified", true, GeneralError.INVALID_PROCESSOR_ACCOUNT),
  BANK_ACCOUNT_NAME_INTERNAL("BANK_ACCOUNT_NAME_INTERNAL", Arrays.asList(ProcessorAccountType.values()), "Internal account name check failed", true, GeneralError.INVALID_PROCESSOR_ACCOUNT),
  BANK_ACCOUNT_NAME_EXTERNAL_SPHONIC("BANK_ACCOUNT_NAME_EXTERNAL_SPHONIC", Arrays.asList(ProcessorAccountType.BANK), "Sphonic IBAN/name validation failed",true, GeneralError.INVALID_PROCESSOR_ACCOUNT),
  DUPLICATE_ACCOUNT("DUPLICATE_ACCOUNT", Arrays.asList(ProcessorAccountType.values()), "Duplicate account", false, GeneralError.DUPLICATE_CARD),
  ACTIVE_ACCOUNT("ACTIVE_ACCOUNT", Arrays.asList(ProcessorAccountType.values()), "Account is not active",false, GeneralError.DUPLICATE_CARD),
  ONE_ACCOUNT_PER_USER("ONE_ACCOUNT_PER_USER", Arrays.asList(ProcessorAccountType.values()), "Account for this processor already exists", false, GeneralError.DUPLICATE_CARD);

  private String name;
  private List<ProcessorAccountType> types;

  public boolean isApplicable(ProcessorAccountType type) {
    return types.stream().anyMatch(t -> t == type);
  }

  private String description;
  private boolean saveAccount;
  private GeneralError generalError;

  @JsonValue
  public String getName() {
    return name;
  }

  public String getDescription() { return description; }

  public boolean isSaveAccount() {
    return saveAccount;
  }

  public GeneralError getGeneralError() {
    return generalError;
  }

  @JsonCreator
  public static ProcessorAccountVerificationType fromName(String name) {
    if (name == null) {
      return null;
    }

    for (ProcessorAccountVerificationType s: ProcessorAccountVerificationType.values()) {
      if (s.getName().equalsIgnoreCase(name)) {
        return s;
      }
    }
    return null;
  }
}
