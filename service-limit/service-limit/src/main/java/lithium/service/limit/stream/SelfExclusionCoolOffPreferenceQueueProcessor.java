package lithium.service.limit.stream;

import lithium.exceptions.Status400BadRequestException;
import lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.limit.client.objects.SelfExclusionCoolOffPreferenceRequest;
import lithium.service.limit.services.SelfExclusionCoolOffPreferenceIngestionService;
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
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
@Data
@EnableBinding({SelfExclusionCoolOffPreferenceQueueSink.class, MigrationExceptionOutputQueue.class})
public class SelfExclusionCoolOffPreferenceQueueProcessor implements IDeadLetterQueueHandler {
    private final SelfExclusionCoolOffPreferenceIngestionService service;

    private final MigrationExceptionOutputQueue migrationExceptionOutputQueue;
    @Value("${lithium.service.limit.self-exclusion-cool-off-preference.dlq-retries:3}")
    private int maxDlqRetries = 3;
    private final RabbitTemplate rabbitTemplate;
    private final String parkingLotQueueName = SelfExclusionCoolOffPreferenceQueueSink.PARKING_LOT;

    @Override
    @Bean
    @Qualifier("selfExclusionCoolOffPreferenceQueueParkingLot")
    public Queue parkingLotQueue() {
        return new Queue(SelfExclusionCoolOffPreferenceQueueSink.PARKING_LOT);
    }

    @StreamListener(SelfExclusionCoolOffPreferenceQueueSink.INPUT)
    private void handle(SelfExclusionCoolOffPreferenceRequest request, @Header(value = "x-retries", required = false, defaultValue = "0") Integer xRetries) throws Exception {
        try {
            service.ingest(request);
        }catch(Exception e) {
            if (xRetries == getMaxDlqRetries()) {
                migrationExceptionOutputQueue.migrationExceptionOutputQueue()
                        .send(MessageBuilder
                                .withPayload(MigrationExceptionRecord.builder()
                                        .customerId(request.getCustomerId().toString())
                                        .migrationType(MigrationType.SELF_EXCLUSION_COOL_OFF_PREFERENCE.type())
                                        .exceptionMessage(e.getMessage())
                                        .requestJson(request.toString())
                                        .build())
                                .build());
            }
            throw new Status400BadRequestException(e.getMessage(), e.getStackTrace());
        }
    }
    @Override
    @RabbitListener(queues = SelfExclusionCoolOffPreferenceQueueSink.DLQ)
    public void dlqHandle(Message failedMessage) {
        IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
    }
}
