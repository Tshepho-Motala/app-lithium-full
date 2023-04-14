package lithium.service.machine.data.objects;

import java.util.List;

import lithium.service.machine.data.entities.LocationDistributionConfigurationRevision;
import lithium.service.machine.data.entities.RelationshipDistributionConfigurationRevision;
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
public class Distribution {
	private List<LocationDistributionConfigurationRevision> locationDistConfigRevisions;
	private List<RelationshipDistributionConfigurationRevision> relationshipDistConfigRevisions;
	private String message;
}
