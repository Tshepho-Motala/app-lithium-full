package lithium.service.user.data.entities.playtimelimit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlayTimeLimitSetRequest {
  private int granularity;
  private long durationInMins;
}
