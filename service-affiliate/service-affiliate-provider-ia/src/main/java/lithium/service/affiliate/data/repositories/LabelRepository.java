package lithium.service.affiliate.data.repositories;

import lithium.service.affiliate.data.entities.Label;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LabelRepository extends PagingAndSortingRepository<Label, Long> {
	Label findOne(Long id);
	Label findByName(String name);
}
