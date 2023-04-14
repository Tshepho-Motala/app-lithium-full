package lithium.service.entity.client.objects;

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
public class EntityType {
	private Long id;
	private String name;
	private String description;
	private Boolean deleted;
	private Domain domain;
}
