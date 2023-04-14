package lithium.service.translate.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.translate.data.entities.Namespace;

public interface NamespaceRepository extends PagingAndSortingRepository<Namespace, Long>, JpaSpecificationExecutor<Namespace> {
	Namespace findByParentAndCode(Namespace parent, String code);
	List<Namespace> findByParent(Namespace parent);

	default Namespace findOne(Long id) {
		return findById(id).orElse(null);
	}
}
