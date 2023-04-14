package lithium.service.access.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessRule {
	private Long id;
	private Domain domain;
	private String name;
	private String description;
	private Action defaultAction;
	private boolean enabled;
	private java.util.List<AccessControlList> accessControlList;
}