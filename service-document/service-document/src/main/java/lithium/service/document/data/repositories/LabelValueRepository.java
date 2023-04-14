package lithium.service.document.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.document.data.entities.Label;
import lithium.service.document.data.entities.LabelValue;

@Deprecated
public interface LabelValueRepository extends PagingAndSortingRepository<LabelValue, Long> {
	LabelValue findByLabelAndValue(Label label, String value);
}
