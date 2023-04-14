package lithium.service.datafeed.provider.google.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import lithium.service.datafeed.provider.google.config.PubSubConfigService;
import lithium.service.datafeed.provider.google.data.IncomingPubSubMessage;
import lithium.service.datafeed.provider.google.exceptions.RabbitConsumerErrorException;
import lithium.service.datafeed.provider.google.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.datafeed.provider.google.exeptions.PubSubInternalErrorException;
import lithium.service.datafeed.provider.google.objects.DataType;
import lithium.service.datafeed.provider.google.objects.PubSubMessage;
import lithium.service.datafeed.provider.google.service.ServicePubSubStream;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Data
@Slf4j
@Service
public class PubSubGoogleServiceUserChangeImpl implements PubSubGoogleService {


    @Autowired
    private PubSubConfigService configService;

    @Autowired
    private PublishersInitService publishersInitService;

    @Autowired
    @Qualifier("logTaskExecutor")
    private TaskExecutor taskExecutor;

    @Autowired
    private ServicePubSubStream pubSubStream;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "service.google.pub-sub.user", durable = "true"),
                    exchange = @Exchange(value = "service.google.pub-sub.user", durable = "true", type = "topic"),
                    key = "service.google.pub-sub.user"
            )
    )
    public void googlePubSubQueueListener(String request) throws RabbitConsumerErrorException {
        try {
            IncomingPubSubMessage message = new ObjectMapper().readValue(request, IncomingPubSubMessage.class);
            if (!configService.isChannelActivated(message.getDomainName(), DataType.ACCOUNT_CHANGES.name())) {
                log.debug("try to consume income rabbit message " + request + "but pub-sub channel is not activated, retry");
                log.warn("Pub-sub channel is not activated: " + DataType.ACCOUNT_CHANGES.name());
                throw new RabbitConsumerErrorException("pub-sub service is not activated");
            }
            sendMessage(request, message.getDomainName());
        } catch (IOException e) {
            log.error("Can't proceed incoming RabbitMQ message, retry " + request + "because exception:" + e.getMessage());
        }
    }

    @Override
    public void sendMessage(String incomeMessage, String domainName) throws IOException {

        Publisher publisher = null;
        if (domainName != null) {

            try {
                publisher = publishersInitService.getPublisher(domainName, DataType.ACCOUNT_CHANGES);
            } catch (Status512ProviderNotConfiguredException e) {
                log.warn(e.getMessage());
            }
            ByteString data = ByteString.copyFromUtf8(incomeMessage);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
            ApiFuture<String> future = null;
            if (publisher != null) {
                try {
                    future = publisher.publish(pubsubMessage);
                    ApiFutures.addCallback(
                            future,
                            new ApiFutureCallback<String>() {
                                @Override
                                public void onFailure(Throwable throwable) {
                                    if (throwable instanceof ApiException) {
                                        ApiException apiException = ((ApiException) throwable);
                                        log.error("cant send message : { " + incomeMessage + "} , to google pub/sub "
                                                + "\n status code : " + apiException.getStatusCode().getCode()
                                                + "\n caused by : " + apiException.getMessage()
                                                + "\n is retryable : " + apiException.isRetryable());
                                    } else {
                                        log.error("cant send message : { " + incomeMessage + "} , to google pub/sub, caused by : " + throwable.getMessage());
                                    }
                                    try {
                                        pubSubStream.processUserChange(new ObjectMapper().readValue(incomeMessage, PubSubMessage.class));
                                    } catch (PubSubInternalErrorException | JsonProcessingException ex) {
                                        log.error("Unable to requeue failed pub-sub message on processUserChange | incomeMessage: {}", incomeMessage);
                                    }
                                }

                                @Override
                                public void onSuccess(String messageId) {
                                    log.info("Published pub-sub message to channel: " + DataType.ACCOUNT_CHANGES.getChannelName() + " with ID: " + messageId);
                                    log.debug("Published pub-sub message to channel: " + DataType.ACCOUNT_CHANGES.getChannelName() + " with ID: " + messageId + "message: " + incomeMessage);
                                }
                            },
                            taskExecutor
                    );
                } finally {
                    publisher.shutdown();
                }
            } else {
                log.error("Pub-sub provider not properly configured for domain:" + domainName + " skipped" + incomeMessage);
            }
        } else {
            log.error("Incoming message doesn't contain domain name, skipped" + incomeMessage);
        }
    }
}
