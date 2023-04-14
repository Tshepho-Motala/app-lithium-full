package lithium.service.promo.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.promo.data.entities.Label;
import lithium.service.promo.data.entities.LabelValue;

public interface LabelValueRepository extends PagingAndSortingRepository<LabelValue, Long> {
	LabelValue findByLabelAndValue(Label label, String value);
}
