package lithium.service.translate.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.translate.data.entities.TranslationValueRevision;

public interface TranslationValueRevisionRepository extends PagingAndSortingRepository<TranslationValueRevision, Long>  {
		
}
