package lithium.service.casino.client.objects.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FreeroundBetConfigs {
	@JsonProperty
	public List<FreeroundBetConfig> bets = new ArrayList<FreeroundBetConfig>();
	@JsonProperty
	public Integer lines;
	@JsonProperty
	public Integer oneCreditBuysLines;
}