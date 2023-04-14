package lithium.service.casino.provider.incentive.services;

import lithium.service.casino.provider.incentive.storage.entities.Event;
import lithium.service.casino.provider.incentive.storage.entities.EventName;
import lithium.service.casino.provider.incentive.storage.repositories.EventNameRepository;
import lithium.service.casino.provider.incentive.storage.repositories.EventRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@Slf4j
public class EventService {

    @Autowired @Setter
    EventNameRepository eventNameRepository;

    @Autowired @Setter
    EventRepository eventRepository;

    public Event findOrCreate(String eventName, Long eventStartTime) {

        EventName eventNameEntity = eventNameRepository.findOrCreateByName(eventName, () -> new EventName());

        Date eventStartTimeDate = new Date(eventStartTime);
        // We drop the milliseconds since some MySQL implementations do not store it, which means
        // a search for an exact match fails
        eventStartTimeDate = Date.from(eventStartTimeDate.toInstant().truncatedTo(ChronoUnit.SECONDS));

        Event event = eventRepository.findByStartTimestampAndEventName(eventStartTimeDate, eventNameEntity);
        log.debug("event service find or create " + event);

        if (event == null) {
            event = new Event();
            event.setEventName(eventNameEntity);
            event.setStartTimestamp(eventStartTimeDate);
            event = eventRepository.save(event);
        }

        log.debug("event findOrCreate " + eventName + " " + eventStartTime + " " + event);

        return event;
    }
}
