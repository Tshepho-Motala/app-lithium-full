package lithium.service.games.services;

import lithium.service.casino.client.objects.FreeGamePayload;
import lithium.service.casino.client.stream.FreeGameStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FreeGameService {
    @Autowired
    private FreeGameStream freeGameStream;

    public void checkBetsOnFreeGame(FreeGamePayload freeGamePayload) {
        log.info("Game received to check if it is DFG: " + freeGamePayload);

        if (freeGamePayload != null && freeGamePayload.getFreeGame()) {
            freeGameStream.checkBetsOnFreeGame(freeGamePayload);

        }
    }
}
