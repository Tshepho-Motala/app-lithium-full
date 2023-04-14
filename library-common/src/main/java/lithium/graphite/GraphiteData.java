package lithium.graphite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@lombok.Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GraphiteData {
	private Tags tags;
	private String[][] datapoints;
	private String target;
}
