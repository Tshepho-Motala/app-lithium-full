package lithium.service.casino.cms.api.schema.lobby;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LobbyRequest {
	private String brand;
	private String channel;
	@JsonProperty("primary_nav_code")
	private String primaryNavCode;
	@JsonProperty("secondary_nav_code")
	private String secondaryNavCode;
}
