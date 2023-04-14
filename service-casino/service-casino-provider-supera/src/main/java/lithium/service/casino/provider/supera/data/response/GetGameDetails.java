package lithium.service.casino.provider.supera.data.response;

import lombok.Data;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@ToString
public class GetGameDetails {
	@JsonProperty("game_url")
	private String gameUrl;
	@JsonProperty
	private String token;
	@JsonProperty
	private GameConfig config;
	@JsonProperty("game_api")
	private String gameApi;
	@JsonProperty("game_base")
	private String gameBase;
}