package lithium.service.reward.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@Accessors(fluent = true)
public class SimpleRewardTypeGame {
    @NotBlank(message = "guid is a required field")
    private String guid;

    @NotBlank(message = "gameId is a required field")
    private String gameId;

    @NotBlank(message = "gameName is a required field")
    private String gameName;
}
