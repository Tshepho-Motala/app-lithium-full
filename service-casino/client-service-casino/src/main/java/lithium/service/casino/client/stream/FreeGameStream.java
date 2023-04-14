package lithium.service.casino.client.stream;

import lithium.service.casino.client.objects.FreeGamePayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class FreeGameStream {
    @Autowired
    private FreeGameStreamOutputQueue channel;

    public void checkBetsOnFreeGame(FreeGamePayload freeGamePayload) {
        channel.outputQueue().send(MessageBuilder.<FreeGamePayload>withPayload(freeGamePayload).build());
    }
}
