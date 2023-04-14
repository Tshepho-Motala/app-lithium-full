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
public class RelationshipDistributionConfiguration {
	private Long id;
	private Relationship relationship;
	private RelationshipDistributionConfigurationRevision current;
}
