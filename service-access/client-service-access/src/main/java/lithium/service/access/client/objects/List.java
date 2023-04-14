package lithium.service.access.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class List {
	private Long id;
	private Domain domain;
	private String name;
	private String description;
	private ListType listType;
	private boolean enabled;
	private java.util.List<Value> values;
}