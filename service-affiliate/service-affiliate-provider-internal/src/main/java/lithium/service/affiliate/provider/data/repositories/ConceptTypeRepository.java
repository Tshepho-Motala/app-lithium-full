package lithium.service.affiliate.provider.data.repositories;

import java.util.List;

import lithium.service.affiliate.provider.data.entities.Concept;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.ConceptType;

public interface ConceptTypeRepository extends PagingAndSortingRepository<ConceptType, Long> {

	public ConceptType findByName(String name);

	public List<ConceptType> listOrderByName();

	default ConceptType findOne(Long id) {
		return findById(id).orElse(null);
	}
	
}