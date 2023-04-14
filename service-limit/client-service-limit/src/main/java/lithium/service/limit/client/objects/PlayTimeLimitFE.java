package lithium.service.limit.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlayTimeLimitFE {
    private String granularity;
    private long playTimeLimit;
    private long playTimeLimitSeconds;
    private long playTimeLimitRemainingSeconds;
    private String type;
}