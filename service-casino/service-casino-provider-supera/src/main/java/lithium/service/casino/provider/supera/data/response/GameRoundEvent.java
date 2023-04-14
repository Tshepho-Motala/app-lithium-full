package lithium.service.casino.provider.supera.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GameRoundEvent {
	@JsonProperty
	public String event;
	@JsonProperty
	public GameRoundContext context;
}