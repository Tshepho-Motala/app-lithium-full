package lithium.service.casino.provider.sportsbook.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Bet {
	private String betId;
}
