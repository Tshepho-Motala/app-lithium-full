package lithium.service.access.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListBasic {
	private String domainName;
	private String name;
	private String description;
	private String type;
	private boolean enabled;
}
