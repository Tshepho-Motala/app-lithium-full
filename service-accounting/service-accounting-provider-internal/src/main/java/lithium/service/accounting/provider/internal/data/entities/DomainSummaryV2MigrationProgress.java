package lithium.service.accounting.provider.internal.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class DomainSummaryV2MigrationProgress {
	@Id
	private Long id;

	@Version
	private int version;

	@Column
	private Long lastTranIdProcessed;

}
