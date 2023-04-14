package lithium.service.casino.cms.api.schema.lobby;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class JackpotWidget extends Widget{

    private String title;

    @JsonProperty("tile_size")
    private String gridSize;

    private String description;

    private List<Progressive> progressives;

    private List<Tile> tiles;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String tileWidgetType;

    private String jackpotLogo;
}
