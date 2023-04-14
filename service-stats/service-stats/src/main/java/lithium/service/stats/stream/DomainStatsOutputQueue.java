package lithium.service.stats.stream;


import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface DomainStatsOutputQueue {

    @Output("domain-stats-output")
    MessageChannel domainStatsOutput();
}
