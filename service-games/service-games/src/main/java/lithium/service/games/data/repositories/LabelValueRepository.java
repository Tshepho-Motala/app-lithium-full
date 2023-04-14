package lithium.service.games.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.games.data.entities.Label;
import lithium.service.games.data.entities.LabelValue;

public interface LabelValueRepository extends PagingAndSortingRepository<LabelValue, Long> {
	LabelValue findByLabelAndValue(Label label, String value);
}
