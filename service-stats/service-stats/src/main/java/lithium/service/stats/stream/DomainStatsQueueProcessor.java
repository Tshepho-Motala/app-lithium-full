package lithium.service.stats.stream;

import lithium.service.stats.client.stream.QueueStatEntry;
import lithium.service.stats.services.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@EnableBinding(DomainStatsQueueSink.class)
public class DomainStatsQueueProcessor {

    private final StatService statService;

    @StreamListener(DomainStatsQueueSink.INPUT)
    public void handle(QueueStatEntry queueStatEntry){
        statService.registerDomainStats(queueStatEntry);
    }
}
