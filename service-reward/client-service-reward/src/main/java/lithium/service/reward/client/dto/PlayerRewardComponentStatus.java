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
public enum PlayerRewardComponentStatus {
  PROCESSING("processing"),
  PROCESSED_EXTERNALLY("processed_externally"),
  PENDING_PLAYER_APPROVAL("pending_player_approval"),
  DECLINED_BY_PLAYER("declined_by_player"),
  REDEEMED("redeemed"),
  AWARDED("awarded"),
  CANCELLED("cancelled"),
  FAILED_EXTERNALLY("failed_externally"),
  FAILED_INTERNALLY("failed_internally");

  @Getter
  @Setter
  @Accessors( fluent = true )
  private String status;

  @JsonValue
  public String getStatus() {
    return status;
  }

  @JsonCreator
  public static PlayerRewardComponentStatus fromStatus(String status) {
    for (PlayerRewardComponentStatus g: PlayerRewardComponentStatus.values()) {
      if (g.status.equalsIgnoreCase(status)) {
        return g;
      }
    }
    return null;
  }
}