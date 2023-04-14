package lithium.service.casino.cms.api.schema.lobby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LobbyResponse {
	private String state;
	private Boolean maintenance;
	private List<Navigation> nav;
	private List<Badge> badges;
	private Page page;
	private Boolean testAccount = false;
	private Boolean freeGamesLocked = true;
}
