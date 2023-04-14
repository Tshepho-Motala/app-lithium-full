package lithium.service.casino.provider.twowinpower.data;

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
public class Game {
	private String uuid;
	private String name;
	private String image;
	private String type;
	private String provider;
	private String technology;
	@JsonProperty("has_lobby")
	private Integer hasLobby;
	@JsonProperty("is_mobile")
	private Integer isMobile;
}
