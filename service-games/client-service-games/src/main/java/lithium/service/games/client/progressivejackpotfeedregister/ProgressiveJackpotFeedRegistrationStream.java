package lithium.service.games.client.progressivejackpotfeedregister;

import lithium.service.games.client.objects.ProgressiveJackpotFeedRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class ProgressiveJackpotFeedRegistrationStream {

    @Autowired
    private ProgressiveJackpotFeedRegistrationOutputQueue channel;

    public void register(ProgressiveJackpotFeedRegistration progressiveJackpotFeedRegistration) {
        Assert.isTrue(channel.outputQueue().send(MessageBuilder.<Object>withPayload(progressiveJackpotFeedRegistration).build()));
    }
}
