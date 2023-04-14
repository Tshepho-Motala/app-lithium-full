package lithium.service.accounting.provider.internal.data.repositories;

import lithium.service.accounting.provider.internal.data.entities.SummaryAccountLabelValueReplayJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummaryAccountLabelValueReplayJobRepository
		extends JpaRepository<SummaryAccountLabelValueReplayJob, Long> {

	default SummaryAccountLabelValueReplayJob findOne(Long id) {
		return findById(id).orElse(null);
	}
}
