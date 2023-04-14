package lithium.transaction;

import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;

public interface IBalanceAdjustmentAccountEffect extends Serializable {

  @JsonValue
  long getAbsValueMultiplier();
}
