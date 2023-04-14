package lithium.service.user.provider.threshold.extremepush.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ThresholdMessage {

  private String message;
  private String domainName;
  private String apptoken;
  private String user_id;
  private String  event;
  private String value;

}
