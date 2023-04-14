package lithium.service.casino.cms.api.schema.lobby.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("tiles")
public class GridWidget extends Widget {
    private String title;
    @JsonProperty("tile_size")
    private String gridSize;
    private String backgroundImage;
    private List<Jackpot> jackpots;
    private List<Tile> tiles;
    private String tileWidgetType;

    @Builder
    public GridWidget(String type, String title, String gridSize, String backgroundImage, List<Jackpot> jackpots, List<Tile> tiles, String tileWidgetType) {
        super(type);
        this.title = title;
        this.gridSize = gridSize;
        this.backgroundImage = backgroundImage;
        this.jackpots = jackpots;
        this.tiles = tiles;
        this.tileWidgetType = tileWidgetType;
    }
}
