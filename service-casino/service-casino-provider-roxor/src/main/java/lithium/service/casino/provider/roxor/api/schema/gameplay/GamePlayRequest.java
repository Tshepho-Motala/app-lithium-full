package lithium.service.casino.provider.roxor.api.schema.gameplay;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GamePlayRequest {
    private String playerId;
    private String website;
    private String gameKey;
    private String platform;
    private String gamePlayId;
    private List<? extends GamePlayOperation> operations;
}
