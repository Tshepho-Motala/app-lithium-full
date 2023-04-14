package lithium.service.accounting.provider.internal.services;

import lithium.service.accounting.provider.internal.config.Properties;
import lithium.service.accounting.provider.internal.stream.AuxLabelQueueSink;
import lithium.service.accounting.provider.internal.stream.TransactionLabelQueueSink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class QueueRateLimiter {
    @Autowired private Properties properties;

    private static final String QUEUE_AUX_LABEL = AuxLabelQueueSink.ORIGINAL_QUEUE;
    private static final String QUEUE_TRANSACTION_LABEL = TransactionLabelQueueSink.ORIGINAL_QUEUE;

    public void limitQueueRateIfApplicable(String queue) {
        boolean enabled = false;
        int minDelayMs = 50;
        int maxDelayMs = 100;
        int delay = 0;

        switch (queue) {
            case QUEUE_AUX_LABEL -> {
                enabled = properties.getQueueRateLimiter().getAuxLabel().isEnabled();
                minDelayMs = properties.getQueueRateLimiter().getAuxLabel().getMinDelayMs();
                maxDelayMs = properties.getQueueRateLimiter().getAuxLabel().getMaxDelayMs();
            }
            case QUEUE_TRANSACTION_LABEL -> {
                enabled = properties.getQueueRateLimiter().getTransactionLabel().isEnabled();
                minDelayMs = properties.getQueueRateLimiter().getTransactionLabel().getMinDelayMs();
                maxDelayMs = properties.getQueueRateLimiter().getTransactionLabel().getMaxDelayMs();
            }
        }

        if (enabled) {
            delay = new Random().nextInt(minDelayMs, maxDelayMs);
        }

        log.trace("QueueRateLimiter.limitQueueRateIfApplicable | queue: {}, enabled: {}, minDelayMs: {}," +
                " maxDelayMs: {}, delay: {}", queue, enabled, minDelayMs, maxDelayMs, delay);

        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                log.error("QueueRateLimiter.limitQueueRateIfApplicable | Unable to implement delay | {}",
                        e.getMessage(), e);
            }
        }
    }
}
