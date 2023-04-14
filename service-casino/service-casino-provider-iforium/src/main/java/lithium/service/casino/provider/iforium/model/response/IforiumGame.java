package lithium.service.casino.provider.iforium.model.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class IforiumGame {
    private String gameId;
    private String title;
    private String integrationProvider;
    private String contentProvider;
    private String channel;
    private String gameType;
    private String genre;
    private String market;
    private String branded;
    private String jackPot;
    private String volatility;
    private String rtp;
    private String releaseDate;
}
