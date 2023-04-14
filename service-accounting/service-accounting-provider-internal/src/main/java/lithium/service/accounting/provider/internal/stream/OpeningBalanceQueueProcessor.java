package lithium.service.accounting.provider.internal.stream;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.objects.BalanceMigrationHistoricDetails;
import lithium.service.accounting.provider.internal.conditional.NotReadOnlyConditional;
import lithium.service.accounting.provider.internal.data.repositories.AccountRepository;
import lithium.service.accounting.provider.internal.services.InitialBalanceIngestionService;
import lithium.service.accounting.provider.internal.services.QueueRateLimiter;
import lithium.service.accounting.provider.internal.services.TransactionServiceWrapper;
import lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.stream.IDeadLetterQueueHandler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
@EnableBinding({OpeningBalanceQueueSink.class, MigrationExceptionOutputQueue.class})
@RequiredArgsConstructor
public class OpeningBalanceQueueProcessor implements IDeadLetterQueueHandler {

    private final TransactionServiceWrapper transactionServiceWrapper;
    private final RabbitTemplate rabbitTemplate;
    private final QueueRateLimiter queueRateLimiter;
    private final ModelMapper mapper;
    private final AccountRepository accountRepository;
    private final InitialBalanceIngestionService ingestionService;
    private final MigrationExceptionOutputQueue migrationExceptionOutputQueue;


    @Value("${lithium.service.accounting.provider.internal.dlqRetries:3}")
    private int maxDlqRetries;
    private final String parkingLotQueueName = OpeningBalanceQueueSink.PARKING_LOT;


    @Override
    @Bean
    @Qualifier("openingBalanceMigrationQueueParkingLot")
    public Queue parkingLotQueue() {
        return new Queue(OpeningBalanceQueueSink.PARKING_LOT);
    }

    @Override
    @RabbitListener(queues = OpeningBalanceQueueSink.DLQ)
    public void dlqHandle(Message failedMessage) {
        IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
    }

    @StreamListener(OpeningBalanceQueueSink.INPUT)
    @TimeThisMethod
    void handle(BalanceMigrationHistoricDetails details, @Header(value = "x-retries", required = false, defaultValue = "0") Integer xRetries)
        throws Status500InternalServerErrorException {

        try {
            ingestionService.initiatePhase1Ingestion(details);
        } catch (Exception e) {
            if (xRetries == getMaxDlqRetries()) {
                log.error("Accounting Balance Phase 1 Migration exception", e);
                migrationExceptionOutputQueue.migrationExceptionOutputQueue()
                        .send(MessageBuilder
                                .withPayload(MigrationExceptionRecord.builder()
                                        .customerId(details.getCustomerId())
                                        .migrationType(MigrationType.OPENING_BALANCE_PHASE1_MIGRATION.type())
                                        .exceptionMessage(e.getMessage())
                                        .requestJson(details.toString())
                                        .build())
                                .build());

            }
            throw new Status500InternalServerErrorException(e.getMessage(), e);
        }
    }

}
