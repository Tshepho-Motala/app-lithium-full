package lithium.service.stats.data.repositories;

import lithium.service.stats.data.entities.Domain;
import lithium.service.stats.data.entities.DomainStat;
import lithium.service.stats.data.entities.Event;
import lithium.service.stats.data.entities.Type;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DomainStatRepository extends PagingAndSortingRepository<DomainStat, Long> {
	DomainStat findByDomainAndTypeAndEvent(Domain domain, Type type, Event event);
}
