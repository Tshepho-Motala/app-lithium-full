package lithium.service.reward.provider.client.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class CancelRewardRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = -57236821767185815L;

  private String domainName;
  private String playerGuid;
  private String referenceId;
  private Long playerRewardTypeHistoryId;
}
