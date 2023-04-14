package lithium.service.games.client.progressivejackpotfeedregister;

import lithium.service.games.client.objects.ProgressiveJackpotFeedRegistration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressiveJackpotFeedRegistrationService {
    private final ProgressiveJackpotFeedRegistrationStream stream;
    private ArrayList<ProgressiveJackpotFeedRegistration> list = new ArrayList<>();

    public void register() {
        for (ProgressiveJackpotFeedRegistration progressiveJackpotFeedRegistration : list) {
            log.info("Sending ProgressiveJackpotFeedRegistration to service games: " + progressiveJackpotFeedRegistration);
            stream.register(progressiveJackpotFeedRegistration);
        }
        list.clear();
    }

    public void create(String moduleName, String gameSupplier) {
        ProgressiveJackpotFeedRegistration progressiveJackpotFeedRegistration = new ProgressiveJackpotFeedRegistration();
        progressiveJackpotFeedRegistration.setModule(moduleName);
        progressiveJackpotFeedRegistration.setGameSupplier(gameSupplier);
        list.add(progressiveJackpotFeedRegistration);
    }

}
