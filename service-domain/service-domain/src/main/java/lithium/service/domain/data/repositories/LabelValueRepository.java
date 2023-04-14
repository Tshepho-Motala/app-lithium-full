package lithium.service.domain.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.domain.data.entities.Label;
import lithium.service.domain.data.entities.LabelValue;

public interface LabelValueRepository extends PagingAndSortingRepository<LabelValue, Long> {
	LabelValue findByLabelAndValue(Label label, String value);
}
