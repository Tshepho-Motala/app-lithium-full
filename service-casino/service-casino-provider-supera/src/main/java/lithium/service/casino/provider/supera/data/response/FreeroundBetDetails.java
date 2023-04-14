package lithium.service.casino.provider.supera.data.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FreeroundBetDetails {
	@JsonProperty
	public Integer id;
	@JsonProperty
	public Integer used;
	@JsonProperty
	public Integer count;
	@JsonProperty
	public List<Integer> betArgs = new ArrayList<Integer>();
	@JsonProperty("bet")
	public FreeroundBet bet;
}