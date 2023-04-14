package lithium.service.event.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.event.entities.EventType;

public interface EventTypeRepository extends PagingAndSortingRepository<EventType, Long> {
	
	EventType findByCode(String code);
	
}
