package lithium.service.games.data.repositories;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.games.data.entities.Label;

public interface LabelRepository extends PagingAndSortingRepository<Label, Long> {

	@CacheEvict({
		"lithium.service.games.data.entities.Label.byId",
		"lithium.service.games.data.entities.Label.byName",
	})
	@Override
	<S extends Label> S save(S entity);

	@Cacheable(value = "lithium.service.games.data.entities.Label.byId", unless = "#result == null")
	default Label findOne(Long id) {
		return findById(id).orElse(null);
	}

	@Cacheable(value = "lithium.service.games.data.entities.Label.byName", unless = "#result == null")
	Label findByName(String name);

}
