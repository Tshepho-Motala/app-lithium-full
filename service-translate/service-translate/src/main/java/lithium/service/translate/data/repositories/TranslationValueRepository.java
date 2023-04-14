package lithium.service.translate.data.repositories;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.entities.TranslationKey;
import lithium.service.translate.data.entities.TranslationValue;
import lithium.service.translate.data.projections.TranslationValueWithoutKey;

import java.util.List;

public interface TranslationValueRepository extends PagingAndSortingRepository<TranslationValue, Long>  {
	
	TranslationValue findByKeyAndLanguage(TranslationKey key, Language language);
	TranslationValue findByKeyAndLanguage_Locale2(TranslationKey key, String locale2);
	
	TranslationValueWithoutKey findWithoutKeyByKeyAndLanguage(TranslationKey key, Language language); 
	TranslationValueWithoutKey findWithoutKeyByKeyAndLanguage_Locale2(TranslationKey key, String locale2);
	
	@Caching(evict = {
			@CacheEvict(cacheNames="lithium.service.translate.services.language.key", allEntries=true),
			@CacheEvict(cacheNames="lithium.service.translate.services.translations.json", allEntries=true)
	})
	@Override
	<S extends TranslationValue> S save(S entity);

	List<TranslationValue> findAllByKeyAndMigratedFalse(TranslationKey key);
}
