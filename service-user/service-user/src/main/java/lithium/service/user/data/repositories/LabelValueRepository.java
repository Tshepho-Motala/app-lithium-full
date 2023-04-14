package lithium.service.user.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.user.data.entities.Label;
import lithium.service.user.data.entities.LabelValue;
import java.util.List;

public interface LabelValueRepository extends PagingAndSortingRepository<LabelValue, Long> {
	LabelValue findByLabelAndValue(Label label, String value);
	LabelValue findByLabel(Label label);

  Page<LabelValue> findAllByLabelIn(List<Label> labels, Pageable page);

  Page<LabelValue> findAllByLabelInAndAndValueLike(List<Label> labels, String name, Pageable page);
}
