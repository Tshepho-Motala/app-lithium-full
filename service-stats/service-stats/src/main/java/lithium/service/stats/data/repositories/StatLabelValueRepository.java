package lithium.service.stats.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.stats.data.entities.Label;
import lithium.service.stats.data.entities.LabelValue;
import lithium.service.stats.data.entities.Stat;
import lithium.service.stats.data.entities.StatLabelValue;

public interface StatLabelValueRepository extends PagingAndSortingRepository<StatLabelValue, Long> {
	StatLabelValue findByLabelValueAndStat(LabelValue labelValue, Stat stat);
	StatLabelValue findByLabelValueLabelAndStat(Label label, Stat stat);
}
