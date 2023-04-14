package lithium.service.reward.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@JsonFormat( shape = JsonFormat.Shape.OBJECT )
@AllArgsConstructor( access = AccessLevel.PRIVATE )
public enum RewardSource implements Serializable {
  SYSTEM("system"),
  BACKOFFICE_SINGLE("backoffice_single"),
  BACKOFFICE_MASS("backoffice_mass"),
  EXTERNAL("external");

  @Setter
  private String source;

  @JsonValue
  public String getSource() {
    return source;
  }

  @JsonCreator
  public static RewardSource fromSource(String source) {
    for (RewardSource g: RewardSource.values()) {
      if (g.source.equalsIgnoreCase(source)) {
        return g;
      }
    }
    return null;
  }
}