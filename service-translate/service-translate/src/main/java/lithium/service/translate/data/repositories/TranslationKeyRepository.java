package lithium.service.translate.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import lithium.service.translate.data.entities.Namespace;
import lithium.service.translate.data.entities.TranslationKey;

public interface TranslationKeyRepository extends PagingAndSortingRepository<TranslationKey, Long>, JpaSpecificationExecutor<TranslationKey>  {
	
	TranslationKey findByNamespaceAndKeyCode(Namespace namespace, String keyCode);
	List<TranslationKey> findByNamespace(Namespace namespace);
	List<TranslationKey> findAllByValuesMigratedFalse();

	default TranslationKey findOne(Long id) {
		return findById(id).orElse(null);
	}
}
