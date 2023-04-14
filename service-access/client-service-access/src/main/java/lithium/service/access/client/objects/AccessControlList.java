package lithium.service.access.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessControlList {
	private Long id;
	private AccessRule accessRule;
	private String action;
	private boolean enabled;
	private int priority;
	private List list;
	private Integer ipResetTime;
	private String message;
}