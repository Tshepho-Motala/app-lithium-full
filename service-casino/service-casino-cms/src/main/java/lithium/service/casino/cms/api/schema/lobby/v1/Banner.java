package lithium.service.casino.cms.api.schema.lobby.v1;

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
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Banner {
	private Long id;
	private String name;
	@JsonProperty("display_text")
	private String displayText;
	@JsonProperty("gameID")
	private String gameId;
	private String url;
	private String image;
	private Long sleep;
	@JsonProperty("terms_url")
	private String termsUrl;
	@JsonProperty("runcount")
	private Integer runCount;
}
