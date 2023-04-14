package lithium.service.casino.provider.supera.data.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FreeroundBets {
	@JsonProperty
	public List<FreeroundBet> bets = new ArrayList<FreeroundBet>();
	@JsonProperty
	public Integer lines;
	@JsonProperty
	public Integer oneCreditBuysLines;
}