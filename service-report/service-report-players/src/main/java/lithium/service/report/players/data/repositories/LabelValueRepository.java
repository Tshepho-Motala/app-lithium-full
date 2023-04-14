package lithium.service.report.players.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.report.players.data.entities.Label;
import lithium.service.report.players.data.entities.LabelValue;

public interface LabelValueRepository extends PagingAndSortingRepository<LabelValue, Long> {
	LabelValue findByLabelAndValue(Label label, String value);
}