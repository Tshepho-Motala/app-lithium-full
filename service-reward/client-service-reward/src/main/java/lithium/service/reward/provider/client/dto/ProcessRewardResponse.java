package lithium.service.reward.provider.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ProcessRewardResponse {
//  private int status;

  @Default
  private String code = "-1";
  private String result;
  private String description;
  private Integer errorCode;
  private Long amountAffected;
  private Long valueUsed; // This will be zero freespins/instant-rewards but for cash rewards it will be total amount the player will receive once granted
  private Long valueGiven; // for freespins/instant-rewards , it will be whatever number of spins issued, for cash it will be the total cash for the reward
  private Long valueInCents; // The cent value for the given amount (e.g 1 free spin is valued at 50 cents), for cash bonuses this will be the same as the amount given

  private String externalReferenceId;
  @Default
  private ProcessRewardStatus status = ProcessRewardStatus.FAILED;
}
