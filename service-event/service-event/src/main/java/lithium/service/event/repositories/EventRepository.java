package lithium.service.event.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.event.entities.Event;

public interface EventRepository extends PagingAndSortingRepository<Event, Long> {
	
	public Event findByUserGuidAndEventTypeCodeAndCurrencyCodeAndDuplicateEventPreventionKey(String userGuid, String eventType, String currency, String duplicateEventPreventionKey);

//	Event findByDomainMachineNameAndGranularityAndCurrencyAndDateStartAndDateEnd(String domain, int granularity,
//			String currency, Date dateStart, Date dateEnd);
	
}