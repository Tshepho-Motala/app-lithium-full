package lithium.service.machine.client.objects;

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
public class Relationship {
	private Long id;
	private Machine machine;
	private RelationshipDistributionConfiguration distributionConfiguration;
	private String entityUuid;
}
