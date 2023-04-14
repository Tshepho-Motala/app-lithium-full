package lithium.service.accounting.provider.internal.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.accounting.provider.internal.data.entities.Label;
import lithium.service.accounting.provider.internal.data.entities.LabelValue;

public interface LabelValueRepository extends PagingAndSortingRepository<LabelValue, Long> {
	LabelValue findByLabelAndValue(Label label, String value);
	Page<LabelValue> findByLabelName(String labelName, Pageable pageable);
}
