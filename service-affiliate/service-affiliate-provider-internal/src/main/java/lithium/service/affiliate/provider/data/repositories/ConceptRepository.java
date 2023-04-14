package lithium.service.affiliate.provider.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.Concept;

public interface ConceptRepository extends PagingAndSortingRepository<Concept, Long> {

	public Concept findByName(String name);

	public List<Concept> listOrderByName();

	default Concept findOne(Long id) {
		return findById(id).orElse(null);
	}


}