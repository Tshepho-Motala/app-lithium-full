package lithium.service.notifications.data.repositories;

import lithium.service.notifications.data.entities.Label;
import lithium.service.notifications.data.entities.LabelValue;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LabelValueRepository extends PagingAndSortingRepository<LabelValue, Long> {

  LabelValue findByLabelAndValue(Label label, String value);
}
