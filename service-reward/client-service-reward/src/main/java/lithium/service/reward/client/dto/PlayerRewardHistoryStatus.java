package lithium.service.reward.client.dto;

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
@JsonFormat( shape = JsonFormat.Shape.OBJECT )
@AllArgsConstructor( access = AccessLevel.PRIVATE )
public enum PlayerRewardHistoryStatus {
  PROCESSING("processing"),
  PENDING("pending"),
  AWARDED("awarded"),
  PARTIALLY_AWARDED("partially_awarded"),
  CANCELLED("cancelled"),
  FAILED("failed"),
  DECLINED_BY_PLAYER("declined"),
  REDEEMED("redeemed");

  @Getter
  @Setter
  @Accessors( fluent = true )
  private String status;

  @JsonValue
  public String getStatus() {
    return status;
  }

  @JsonCreator
  public static PlayerRewardHistoryStatus fromStatus(String status) {
    for (PlayerRewardHistoryStatus g: PlayerRewardHistoryStatus.values()) {
      if (g.status.equalsIgnoreCase(status)) {
        return g;
      }
    }
    return null;
  }
}