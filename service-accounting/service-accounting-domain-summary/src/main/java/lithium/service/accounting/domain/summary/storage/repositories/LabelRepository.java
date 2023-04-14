package lithium.service.accounting.domain.summary.storage.repositories;

import lithium.service.accounting.domain.summary.storage.entities.Label;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {
	@CacheEvict({
		"lithium.service.accounting.domain.summary.storage.entities.Label.byId",
		"lithium.service.accounting.domain.summary.storage.entities.Label.byName",
	})
	@Override
	<S extends Label> S save(S entity);

	@Cacheable(value = "lithium.service.accounting.domain.summary.storage.entities.Label.byId",
			unless = "#result == null")
	default Label findOne(Long id) {
		return findById(id).orElse(null);
	}

	@Cacheable(value = "lithium.service.accounting.domain.summary.storage.entities.Label.byName",
			unless = "#result == null")
	Label findByName(String name);
}
