package lithium.service.reward.provider.casino.roxor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorStatus {

  private String code; //ErrorCode
  private ErrorObject error;
}
