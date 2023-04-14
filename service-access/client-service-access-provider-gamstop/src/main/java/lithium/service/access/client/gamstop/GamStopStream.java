package lithium.service.access.client.gamstop;

import lithium.service.access.client.gamstop.objects.BatchExclusionCheckRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GamStopStream {
    @Autowired
    private GamstopStreamOutputQueue channel;

    public void process(BatchExclusionCheckRequest batchExclusionCheckRequest) {
        try {
            channel.channel().send(MessageBuilder.withPayload(batchExclusionCheckRequest).build());
        } catch (RuntimeException re) {
            log.error(re.getMessage(), re);
        }
    }
}
