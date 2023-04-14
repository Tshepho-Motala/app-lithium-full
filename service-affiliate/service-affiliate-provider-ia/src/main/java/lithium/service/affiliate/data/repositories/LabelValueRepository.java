package lithium.service.affiliate.data.repositories;

import lithium.service.affiliate.data.entities.Label;
import lithium.service.affiliate.data.entities.LabelValue;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LabelValueRepository extends PagingAndSortingRepository<LabelValue, Long> {
	LabelValue findByLabelAndValue(Label label, String value);
}
