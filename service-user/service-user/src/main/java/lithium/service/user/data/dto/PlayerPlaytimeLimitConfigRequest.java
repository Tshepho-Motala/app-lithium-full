package lithium.service.user.data.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerPlaytimeLimitConfigRequest {
  private long userId;
  private long granularity;
  private long secondsAllocated;
  private LocalDateTime createdDate;
}
