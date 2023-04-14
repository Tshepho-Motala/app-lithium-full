package lithium.service.accounting.provider.internal.stream;

import lithium.exceptions.Status404UserNotFoundException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.objects.BalanceMigrationHistoricDetails;
import lithium.service.accounting.provider.internal.conditional.NotReadOnlyConditional;
import lithium.service.accounting.provider.internal.services.InitialBalanceIngestionService;
import lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.stream.IDeadLetterQueueHandler;
import lombok.Data;
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
import org.springframework.context.annotation.Conditional;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Data
@Conditional(NotReadOnlyConditional.class)
@EnableBinding({UpdatingBalanceQueueSink.class, MigrationExceptionOutputQueue.class})
public class UpdatingBalanceQueueProcessor implements IDeadLetterQueueHandler {

    private final RabbitTemplate rabbitTemplate;
    private final InitialBalanceIngestionService ingestionService;
    private final MigrationExceptionOutputQueue migrationExceptionOutputQueue;

    @Value("${lithium.service.accounting.provider.internal.dlqRetries:3}")
    private int maxDlqRetries;
    private final String parkingLotQueueName = UpdatingBalanceQueueSink.PARKING_LOT;


    @Override
    @Bean
    @Qualifier("updatingBalanceMigrationQueueParkingLot")
    public Queue parkingLotQueue() {
        return new Queue(UpdatingBalanceQueueSink.PARKING_LOT);
    }

    @Override
    @RabbitListener(queues = UpdatingBalanceQueueSink.DLQ)
    public void dlqHandle(Message failedMessage) {
        IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
    }

    @StreamListener(UpdatingBalanceQueueSink.INPUT)
    @TimeThisMethod
    void handle(BalanceMigrationHistoricDetails details, @Header(value = "x-retries", required = false, defaultValue = "0") Integer xRetries)
        throws Status500InternalServerErrorException {

        try {
            ingestionService.initiatePhase2Ingestion(details);
        } catch (Exception e) {
            if (xRetries == getMaxDlqRetries()) {
                log.error("Accounting Balance Phase 2 exception", e);
                migrationExceptionOutputQueue.migrationExceptionOutputQueue()
                        .send(MessageBuilder
                                .withPayload(MigrationExceptionRecord.builder()
                                        .customerId(details.getCustomerId())
                                        .migrationType(MigrationType.OPENING_BALANCE_PHASE2_MIGRATION.type())
                                        .exceptionMessage(e.getMessage())
                                        .requestJson(details.toString())
                                        .build())
                                .build());

            }
            throw new Status500InternalServerErrorException(e.getMessage(), e);
        }
    }

}
