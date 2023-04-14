package lithium.service.stats.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.stats.data.entities.Stat;

public interface StatRepository extends PagingAndSortingRepository<Stat, Long> {
	Stat findByName(String name);
}