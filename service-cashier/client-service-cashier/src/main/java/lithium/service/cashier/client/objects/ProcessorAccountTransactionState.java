package lithium.service.cashier.client.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@AllArgsConstructor()
public enum ProcessorAccountTransactionState {
  CREATED("CREATED"),
  PENDING("PENDING"),
  SUCCESS("SUCCESS"),
  FAILED("FAILED"),
  CANCELED("CANCELED");

  private String name;

  @JsonValue
  public String getName() {
    return name;
  }

  @JsonCreator
  public static ProcessorAccountTransactionState fromName(String name) {
    for (ProcessorAccountTransactionState s: ProcessorAccountTransactionState.values()) {
      if (s.getName().equalsIgnoreCase(name)) {
        return s;
      }
    }
    return null;
  }
}
