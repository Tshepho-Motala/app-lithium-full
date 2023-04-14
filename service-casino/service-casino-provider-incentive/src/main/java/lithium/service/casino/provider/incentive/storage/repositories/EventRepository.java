package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.service.casino.provider.incentive.storage.entities.Event;
import lithium.service.casino.provider.incentive.storage.entities.EventName;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface EventRepository extends PagingAndSortingRepository<Event, Long> {

    Event findByStartTimestampAndEventName(Date startTimestamp, EventName eventName);

}
