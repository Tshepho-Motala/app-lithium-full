package lithium.service.casino.provider.twowinpower.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameListResponse {
	@JsonProperty("items")
	private List<Game> items;
	
	@JsonProperty("_links")
	private Links links;
	
	@JsonProperty("_meta")
	private Meta meta;
}