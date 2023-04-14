package lithium.service.casino.data.objects;

import java.util.List;

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
public class PlayerBonusHistoryPage {
	private List<PlayerBonusHistoryDisplay> list;
	private boolean hasMore;
}