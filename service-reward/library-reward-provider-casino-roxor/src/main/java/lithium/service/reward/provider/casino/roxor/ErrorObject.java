package lithium.service.reward.provider.casino.roxor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorObject {

  private String displayMessage;
  private Integer category; //ErrorCategory
}
