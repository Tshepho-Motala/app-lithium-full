package lithium.service.user.client.objects;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerPlaytimeLimitV2ConfigRevisionDto {
  private long id;
  private User user;
  private Granularity granularity;
  private User createdBy;
  private LocalDateTime createdDate;
  private LocalDateTime effectiveFrom;
  private long secondsAllocated;
}
