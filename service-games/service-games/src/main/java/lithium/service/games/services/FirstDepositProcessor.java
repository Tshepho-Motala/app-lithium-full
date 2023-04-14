package lithium.service.games.services;

import lithium.service.cashier.client.event.ICashierFirstDepositProcessor;
import lithium.service.cashier.client.objects.SuccessfulTransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FirstDepositProcessor implements ICashierFirstDepositProcessor {
    @Autowired
    private GameUserStatusService userStatusService;
    @Override
    public void processFirstDeposit(SuccessfulTransactionEvent request) throws Exception {
        log.info("First deposit event is received: " + request);
        try {
            userStatusService.unlockAllFreeGames(request.getUserGuid(), null);
        } catch (Throwable e) {
            log.error("Failed to unlock free games processing first deposit event: " + request + "Exception: " + e.getMessage());
        }

    }
}
