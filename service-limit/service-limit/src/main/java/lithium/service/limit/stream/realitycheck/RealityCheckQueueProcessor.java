package lithium.service.limit.stream.realitycheck;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.limit.client.objects.RealityCheckMigrationDetails;
import lithium.service.limit.services.RealityCheckIngestionService;
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
@EnableBinding({RealityCheckQueueSink.class, MigrationExceptionOutputQueue.class})
@RequiredArgsConstructor
public class RealityCheckQueueProcessor implements IDeadLetterQueueHandler {

    private final MigrationExceptionOutputQueue migrationExceptionOutputQueue;
    @Value("${lithium.service.limit.dlqRetries:3}")
    private int maxDlqRetries;
    private final RabbitTemplate rabbitTemplate;

    private final RealityCheckIngestionService realityCheckIngestionService;
    private final String parkingLotQueueName = RealityCheckQueueSink.PARKING_LOT;

    @Override
    @Bean
    @Qualifier("realityCheckParkingLot")
    public Queue parkingLotQueue() {
        return new Queue(RealityCheckQueueSink.PARKING_LOT);
    }

    @Override
    @RabbitListener(queues = RealityCheckQueueSink.DLQ)
    public void dlqHandle(Message failedMessage) {
        IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
    }

    @StreamListener(RealityCheckQueueSink.INPUT)
    void handle(RealityCheckMigrationDetails details, @Header(value = "x-retries", required = false, defaultValue = "0") Integer xRetries) throws Status500InternalServerErrorException {
        log.debug("Received reality checks via queue: {}", details);
        try {
            realityCheckIngestionService.initiateRealityCheck(details);
        } catch (Exception e) {
            if (xRetries == getMaxDlqRetries()) {
                log.error("Reality check exception: {}", e.getMessage(), e);
                migrationExceptionOutputQueue.migrationExceptionOutputQueue()
                        .send(MessageBuilder
                                .withPayload(MigrationExceptionRecord.builder()
                                        .customerId(details.getCustomerID())
                                        .migrationType(MigrationType.REALITY_CHECK_MIGRATION.type())
                                        .exceptionMessage(e.getMessage())
                                        .requestJson(details.toString())
                                        .build())
                                .build());
            }
            throw new Status500InternalServerErrorException(e.getMessage(), e);
        }
    }

}
