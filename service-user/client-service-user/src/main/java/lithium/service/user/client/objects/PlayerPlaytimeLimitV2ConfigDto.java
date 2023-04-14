package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerPlaytimeLimitV2ConfigDto {
  private Long id;
  private User user;
  private PlayerPlaytimeLimitV2ConfigRevisionDto currentConfigRevision;
  private PlayerPlaytimeLimitV2ConfigRevisionDto pendingConfigRevision;
}
