package lithium.service.machine.client.objects;

import java.math.BigDecimal;
import java.util.Date;

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
public class RelationshipDistributionConfigurationRevision {
	private Long id;
	private BigDecimal percentage;
	private Date start;
	private Date end;
	private RelationshipDistributionConfiguration relationshipDistributionConfiguration;
}
