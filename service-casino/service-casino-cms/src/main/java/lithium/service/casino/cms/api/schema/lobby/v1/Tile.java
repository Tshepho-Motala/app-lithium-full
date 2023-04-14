package lithium.service.casino.cms.api.schema.lobby.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tile {
	private String type;
	@JsonProperty("gameID")
	private String gameId;
	private String gameName;
	private String commercialGameName;
	private String image;
	private String badge;
	private BigDecimal balance;
	private String balanceCurrency;
	@JsonProperty("runcount")
	private Integer runCount;
	private String promoId;
}
