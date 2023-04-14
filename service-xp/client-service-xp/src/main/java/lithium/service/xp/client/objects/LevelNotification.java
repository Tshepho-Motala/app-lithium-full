package lithium.service.xp.client.objects;

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
public class LevelNotification {
	private Long id;
	private int version;
	private Level level;
	private Long triggerPercentage;
	private String notificationName;
}
