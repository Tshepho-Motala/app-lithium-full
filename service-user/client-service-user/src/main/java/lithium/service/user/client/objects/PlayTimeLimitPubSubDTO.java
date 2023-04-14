package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlayTimeLimitPubSubDTO {
    private int granularity;
    private long playTimeLimit;
    private String type;
    private long playTimeLimitSeconds;
    private long playTimeLimitRemainingSeconds;

    public PlayTimeLimitPubSubDTO(int granularity, long playTimeLimit, long playTimeLimitRemainingSeconds, String type) {
        this.init(granularity, playTimeLimit, playTimeLimitRemainingSeconds, type);
    }

    private void init(int granularity, long playTimeLimit, long playTimeLimitRemainingSeconds, String type) {
        this.granularity = granularity;
        this.playTimeLimit = playTimeLimit;
        if(type != null) {
            this.type = type;
        }
        this.playTimeLimitSeconds = playTimeLimit * 60;
        this.playTimeLimitRemainingSeconds = playTimeLimitRemainingSeconds;
    }

}
