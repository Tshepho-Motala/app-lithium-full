package lithium.service.casino.provider.sportsbook.request;


import lithium.service.casino.provider.sportsbook.data.Bet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;

@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class BetInfoRequest {
	private ArrayList<Bet> bets;
	private Long timestamp;
	private String sha256;
}
