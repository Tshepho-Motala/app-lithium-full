package lithium.service.user.client.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PubSubMarketingPreferences implements PubSubObj {
    private String domain;
    private long accountId;
    private PubSubEventType eventType;
    private String origin;
    private String guid;

    private Boolean emailOptOut;
    private Boolean postOptOut;
    private Boolean smsOptOut;
    private Boolean pushOptOut;
    private Boolean leaderBoardOptOut;
    private Boolean promotionsOptOut;
    private Boolean callOptOut;
}
