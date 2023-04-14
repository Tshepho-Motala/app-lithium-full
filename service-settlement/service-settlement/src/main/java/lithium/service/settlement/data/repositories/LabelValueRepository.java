package lithium.service.settlement.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.settlement.data.entities.Label;
import lithium.service.settlement.data.entities.LabelValue;

public interface LabelValueRepository extends PagingAndSortingRepository<LabelValue, Long> {
	LabelValue findByLabelAndValue(Label label, String value);
}