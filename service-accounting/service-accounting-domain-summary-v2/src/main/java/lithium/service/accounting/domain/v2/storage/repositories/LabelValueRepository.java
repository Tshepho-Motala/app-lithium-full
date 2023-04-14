package lithium.service.accounting.domain.v2.storage.repositories;

import lithium.service.accounting.domain.v2.storage.entities.Label;
import lithium.service.accounting.domain.v2.storage.entities.LabelValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelValueRepository extends JpaRepository<LabelValue, Long> {
	LabelValue findByLabelAndValue(Label label, String value);
	Page<LabelValue> findByLabelName(String labelName, Pageable pageable);
}
