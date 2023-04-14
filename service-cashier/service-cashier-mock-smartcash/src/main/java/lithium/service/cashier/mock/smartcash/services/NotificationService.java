package lithium.service.cashier.mock.smartcash.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.mock.smartcash.configuration.SmartcashConfigurationProperties;
import lithium.service.cashier.mock.smartcash.data.Scenario;
import lithium.service.cashier.mock.smartcash.data.entities.Transaction;
import lithium.service.cashier.processor.smartcash.SmartcashCallbackTransaction;
import lithium.service.cashier.processor.smartcash.SmartcashEncryptor;
import lithium.service.cashier.processor.smartcash.data.SmartcashCallbackData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class NotificationService {
    private final String FAILED_SIGNATURE = "c9qRkRq/KVexlr5jVbSBAb3S/yPZUQ4A1NydzdWlv4jTjISUbnY7smW4NeKvTL2/r4ng4X0ex3tPqwq9IQ2KEoXpJgHc28aGoemmigGVqXNmwsmTH4GSf5a4lW8JieFVzP/1YOg2WRGbQyubM8zroDqnQqWM/Hh0PP/RKyp/bEIZRtmohAmelK+JXi6BLkNffLyKlHvcutzRdUXaboCFQ22bMhdzlLLy9xucahewAi25T9MZ95UDuojYP6ryuooUJhQMclxCqc+eXH9nHOpUjWgRhji4G3qb+10i5Jocdabxnx76vH+w4S8CGhkSh607Q/SZmPujEYyWyKYji0dZrQ==";

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private WebhookService webhookService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SmartcashConfigurationProperties configuration;

    @Autowired
    LithiumConfigurationProperties properties;

    public void notify(final Transaction transactionEntity, Scenario scenario) {
        try {

            SmartcashCallbackData callbackData = mapCallbackData(transactionEntity);

            Long delay = configuration.getNotificationDelay();
            String hash = SmartcashEncryptor.encryptCallback(mapper.writeValueAsString(callbackData), configuration.getHashKey());
            switch (scenario) {
                case NO_NOTIFICATION:
                case NO_PLAYER_CONFIRMATION:
                    return;
                case NO_NOTIFICATION_DELAY:
                    delay = 0L;
                    break;
                case NO_NOTIFICATION_SIGNATURE:
                    hash = null;
                    break;
                case FAILED_NOTIFICATION_SIGNATURE:
                    hash = FAILED_SIGNATURE;
                    break;
                default:
                    break;
            }
            callbackData.setHash(hash);
            String notificationData = mapper.writeValueAsString(callbackData);
            taskScheduler.schedule( () -> {
                try {
                    webhookService.callWebhook(getNotificationUrl(), notificationData);
                } catch (Exception ex) {
                    log.error("Failed to send notification for transaction:" + transactionEntity.getReference() + " Url: " + getNotificationUrl() + " Data:" + notificationData + " Exception: " + ex.getMessage(), ex);
                }
            }, new Date(System.currentTimeMillis() + delay));
        } catch (Exception ex) {
            log.error("Failed to init notification for transaction: " + transactionEntity.getReference() + " Scenario: " + scenario.name() + "Exception: " + ex.getMessage(), ex);
        }
    }

    private String getNotificationUrl() {
        return properties.getGatewayPublicUrl() + configuration.getNotificationUrl();
    }

    private SmartcashCallbackData mapCallbackData(Transaction transactionEntity) {
        return SmartcashCallbackData.builder()
            .transaction(SmartcashCallbackTransaction.builder()
                .code(transactionEntity.getScenario().getCode())
                .referenceId("SMARTCASHMOCK" + transactionEntity.getReference())
                .message(transactionEntity.getMessage())
                .transactionId(transactionEntity.getReference())
                .status_code(transactionEntity.getStatus().name())
                .airtelMoneyId("MOCK_SMARTCASH_" + transactionEntity.getReference())
                .build())
        .build();
    }

}
