package lithium.service.reward.provider.casino.roxor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true )
public class CancelRewardResponse {

  private CancelStatus status;
  private String rewardId;
  private Integer numberOfCancelledUnit;
}
