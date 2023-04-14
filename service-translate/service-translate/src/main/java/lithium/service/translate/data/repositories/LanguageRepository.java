package lithium.service.translate.data.repositories;

import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.translate.data.entities.Language;

public interface LanguageRepository extends PagingAndSortingRepository<Language, Long>, JpaSpecificationExecutor<Language> {
	List<Language> findByEnabled(boolean enabled);
	Language findByLocale3(String locale3);
	Language findByLocale2(String locale2);

	@Override
	@Caching(evict = {
			@CacheEvict(value = "lithium.service.translate.services.languages", allEntries = true)
	})
	<S extends Language> S save(S arg0);

	default Language findOne(Long id) {
		return findById(id).orElse(null);
	}
}
