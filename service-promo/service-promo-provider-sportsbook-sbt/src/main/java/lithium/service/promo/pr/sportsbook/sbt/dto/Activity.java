package lithium.service.promo.pr.sportsbook.sbt.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lithium.service.promo.client.dto.IActivity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Activity implements IActivity {
  WIN("win"),
  BET("bet");

  @Getter
  @Setter
  private String activity;

  @JsonCreator
  public static Activity fromActivity(String activity) {
    for (Activity a: Activity.values()) {
      if (a.activity.equalsIgnoreCase(activity)) {
        return a;
      }
    }
    return null;
  }
}
