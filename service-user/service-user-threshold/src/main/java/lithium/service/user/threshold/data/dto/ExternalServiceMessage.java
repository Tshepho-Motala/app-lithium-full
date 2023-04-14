package lithium.service.user.threshold.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ExternalServiceMessage {

  private String message;
  private String domainName;
  private String apptoken;
  @JsonProperty( "user_id" )
  private String userId;
  private String event;
  private String value;

}
