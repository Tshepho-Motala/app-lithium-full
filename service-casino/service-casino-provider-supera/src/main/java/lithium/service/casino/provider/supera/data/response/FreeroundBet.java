package lithium.service.casino.provider.supera.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FreeroundBet {
	@JsonProperty
	public Integer bet;
	@JsonProperty
	public Integer lines;
	@JsonProperty
	public Integer betMultiplier;
	@JsonProperty
	public Integer betId;
}