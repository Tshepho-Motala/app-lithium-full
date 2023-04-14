package lithium.service.limit.client.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lithium.util.StringUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;

@ToString
@JsonFormat(shape = Shape.STRING)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum LossLimitsVisibility {
  /**
   * do not display anything to player (can and will be enabled upon next event/manually)
   */
  DISABLED(0),
  /**
   * display loss limits to player
   */
  ENABLED(1),
  /**
   * no more notifications going out to player, no visibility of limits on FE (system call from svc-limit/threshold to be ignored.),
   * will only be able to enable manually from LBO. (If toggled in LBO, then normal flow continues)
   */
  OFF(2);

  @Getter
  @Setter
  @Accessors(fluent = true)
  private int visibility;

  @JsonCreator
  public static LossLimitsVisibility fromName(String name) {
    for (LossLimitsVisibility llv: LossLimitsVisibility.values()) {
      if (StringUtils.equals(llv.name(), name)) {
        return llv;
      }
    }
    return null;
  }
  public static LossLimitsVisibility fromVisibility(int visibility) {
    for (LossLimitsVisibility tt : LossLimitsVisibility.values()) {
      if (tt.visibility == (visibility)) {
        return tt;
      }
    }
    return null;
  }
}
