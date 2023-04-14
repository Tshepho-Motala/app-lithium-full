package lithium.service.limit.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class RestrictionEvent {
	private Integer id;
	private String event;
	private String displayName;
}
