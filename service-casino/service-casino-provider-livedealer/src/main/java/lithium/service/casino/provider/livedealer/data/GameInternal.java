package lithium.service.casino.provider.livedealer.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GameInternal {
	private String id;
	private String name;
	private String platform;
	private String category;
	private String options;
}
