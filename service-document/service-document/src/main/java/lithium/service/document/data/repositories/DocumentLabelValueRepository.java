package lithium.service.document.data.repositories;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.document.data.entities.DocumentLabelValue;

@Deprecated
public interface DocumentLabelValueRepository extends PagingAndSortingRepository<DocumentLabelValue, Long> {
	
	@Cacheable(key="#root.args[0]", cacheNames="lithium.service.document.data.repositories.findByDocumentId", unless="#result == null")
	List<DocumentLabelValue> findByDocumentId(long documentId);
	
	@Cacheable(key="#root.args[0]", cacheNames="lithium.service.document.data.repositories.findByDocumentIdAndDeletedFalse", unless="#result == null")
	List<DocumentLabelValue> findByDocumentIdAndDeletedFalse(long documentId);
	
	DocumentLabelValue findByDocumentIdAndLabelValueId(long documentId, long labelValueId);
	
	DocumentLabelValue findByDocumentIdAndLabelValueLabelNameAndDeletedFalse(long docIumentd, String labelName);
	
	@Override
	@Caching(evict = {
		@CacheEvict(cacheNames={"lithium.service.document.data.repositories.findByDocumentIdAndDeletedFalse",
							"lithium.service.document.data.repositories.findByDocumentId"}, key="#root.args[0].getDocumentId()")
		})
	public <S extends DocumentLabelValue> S save(S entity);
}
