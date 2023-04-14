package lithium.service.casino.provider.supera.data.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@ToString
public class Game {
	@JsonProperty
	private String id;
	@JsonProperty
	private String name;
	@JsonProperty
	private String alias;
	@JsonProperty
	private String brand;
	@JsonProperty
	private List<Integer> rtp = new ArrayList<Integer>();
	@JsonProperty
	private Integer lines;
}