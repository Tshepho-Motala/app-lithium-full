package lithium.service.casino.provider.sportsbook.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.casino.provider.sportsbook.data.BetInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class BetInfoResponse {
	@JsonProperty("urls")
	private ArrayList<BetInfo> betInfos;
	private Long timestamp;
	private String sha256;
	private String error;
}
