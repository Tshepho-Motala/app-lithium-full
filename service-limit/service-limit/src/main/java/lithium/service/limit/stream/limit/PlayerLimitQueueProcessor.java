package lithium.service.limit.stream.limit;

import lithium.exceptions.Status400BadRequestException;
import lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.limit.client.objects.PlayerLimitPreferenceMigrationDetails;
import lithium.service.limit.services.PlayerLimitPreferencesIngestionService;
import lithium.service.limit.stream.MigrationExceptionOutputQueue;
import lithium.stream.IDeadLetterQueueHandler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
@Data
@EnableBinding({PlayerLimitQueueSink.class, MigrationExceptionOutputQueue.class})
@RequiredArgsConstructor
public class PlayerLimitQueueProcessor implements IDeadLetterQueueHandler {

    @Value("${lithium.service.limit.dlqRetries:3}")
    private int maxDlqRetries;
    private final RabbitTemplate rabbitTemplate;
    private final PlayerLimitPreferencesIngestionService playerLimitPreferencesIngestionService;
    private final MigrationExceptionOutputQueue migrationExceptionOutputQueue;

    private final String parkingLotQueueName = PlayerLimitQueueSink.PARKING_LOT;



    @Override
    @Bean
    @Qualifier("playerLimitQueueParkingLot")
    public Queue parkingLotQueue() {
        return new Queue(PlayerLimitQueueSink.PARKING_LOT);
    }

    @Override
    @RabbitListener(queues = PlayerLimitQueueSink.DLQ)
    public void dlqHandle(Message failedMessage) {
        IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
    }

    @StreamListener(PlayerLimitQueueSink.INPUT)
    void handle(PlayerLimitPreferenceMigrationDetails details, @Header(value = "x-retries", required = false, defaultValue = "0") Integer xRetries){
        try {
            log.debug("Received player limits preferences via queue: {}", details);
            playerLimitPreferencesIngestionService.initiatePlayerLimitPreferences(details);
        } catch (Exception e){
            if (xRetries == getMaxDlqRetries()){
                log.error("Player preference exception: {}", e.getMessage(), e);
                migrationExceptionOutputQueue.migrationExceptionOutputQueue()
                        .send(MessageBuilder
                                .withPayload(MigrationExceptionRecord.builder()
                                        .customerId(details.getCustomerID())
                                        .migrationType(MigrationType.PLAYER_LIMIT_PREFERENCES_MIGRATION.type())
                                        .exceptionMessage(e.getMessage())
                                        .requestJson(details.toString())
                                        .build())
                                .build());
            }
            throw new Status400BadRequestException(e.getMessage(), e.getStackTrace());
        }
    }
}
