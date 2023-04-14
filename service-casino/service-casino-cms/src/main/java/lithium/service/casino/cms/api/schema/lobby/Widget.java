package lithium.service.casino.cms.api.schema.lobby;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY,
	property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = BannerWidget.class, name = "banner"),
	@JsonSubTypes.Type(value = TileWidget.class, name = "tiles"),
	@JsonSubTypes.Type(value = GridWidget.class, name = "grid"),
	@JsonSubTypes.Type(value = TopGamesWidget.class, name = "topGames"),
	@JsonSubTypes.Type(value = AtoZWidget.class, name = "atoz"),
	@JsonSubTypes.Type(value = JackpotGridWidget.class, name = "jackpotGrid"),
	@JsonSubTypes.Type(value = JackpotTileWidget.class, name = "jackpotTile"),
	@JsonSubTypes.Type(value = DfgWidget.class, name = "dfg")


})
public abstract class Widget {
	private String type;
}
