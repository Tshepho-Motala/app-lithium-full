package lithium.service.accounting.domain.summary.storage.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import java.time.LocalDate;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryReconciliation {
	@Id
	private long id;

	@Version
	private int version;

	@Column
	private LocalDate lastDateProcessed;
}
