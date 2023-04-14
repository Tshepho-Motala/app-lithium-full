package lithium.service.casino.provider.supera.data.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EventActions {
	@JsonProperty
	public List<GameRoundEvent> events = new ArrayList<GameRoundEvent>();
	@JsonProperty
	public List<GameRoundAction> actions = new ArrayList<GameRoundAction>();
}