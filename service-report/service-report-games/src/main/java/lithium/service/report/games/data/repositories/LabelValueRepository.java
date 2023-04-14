package lithium.service.report.games.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.report.games.data.entities.Label;
import lithium.service.report.games.data.entities.LabelValue;

public interface LabelValueRepository extends PagingAndSortingRepository<LabelValue, Long> {
	LabelValue findByLabelAndValue(Label label, String value);
}