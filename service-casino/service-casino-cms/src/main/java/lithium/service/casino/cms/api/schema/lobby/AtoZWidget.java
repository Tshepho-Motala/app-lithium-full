package lithium.service.casino.cms.api.schema.lobby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("atoz")
public class AtoZWidget extends Widget{
    private String title;
    @JsonProperty("tile_size")
    private String tileSize;
    private String backgroundImage;
    private List<Jackpot> jackpots;
    private List<Tile> tiles;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String tileWidgetType;

    @Builder
    public AtoZWidget(String type, String title, String tileSize, String backgroundImage, List<Jackpot> jackpots, List<Tile> tiles, String tileWidgetType) {
        super(type);
        this.title = title;
        this.tileSize = tileSize;
        this.backgroundImage = backgroundImage;
        this.jackpots = jackpots;
        this.tiles = tiles;
        this.tileWidgetType = tileWidgetType;
    }

}

