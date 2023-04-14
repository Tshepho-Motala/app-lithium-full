package lithium.service.casino.provider.supera.data.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@ToString
public class GameConfig {
	@JsonProperty
	private String scale;
	@JsonProperty
	private List<Integer> betMultipliers = new ArrayList<Integer>();
	@JsonProperty
	private Integer initialBetMultiplierIndex;
}