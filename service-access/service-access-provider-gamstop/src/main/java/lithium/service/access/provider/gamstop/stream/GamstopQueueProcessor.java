package lithium.service.access.provider.gamstop.stream;

import lithium.service.access.client.gamstop.objects.BatchExclusionCheckRequest;
import lithium.service.access.provider.gamstop.services.ApiService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(GamstopQueueSink.class)
@Slf4j
public class GamstopQueueProcessor {

    @Autowired
    private ApiService apiService;

    @StreamListener(GamstopQueueSink.INPUT)
    public void handle(BatchExclusionCheckRequest batchExclusionCheckRequest) {
        log.debug("Batch received from queue for processing  {}", batchExclusionCheckRequest);
        try {
            apiService.batchExclusionCheck(batchExclusionCheckRequest);
        } catch (Exception | UserClientServiceFactoryException ex) {
            log.error("Error handling batch exclusion request {}", ex);
        }
    }
}
