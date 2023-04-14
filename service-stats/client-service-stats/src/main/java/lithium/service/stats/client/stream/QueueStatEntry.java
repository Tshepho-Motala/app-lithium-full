package lithium.service.stats.client.stream;

import java.util.Map;
import lithium.service.stats.client.enums.Event;
import lithium.service.stats.client.enums.Type;
import lithium.service.stats.client.objects.StatEntry;
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
public class QueueStatEntry {
	private String type;
	private String event;
	private StatEntry entry;
	private Map<String, String> passThroughInfo;
}
