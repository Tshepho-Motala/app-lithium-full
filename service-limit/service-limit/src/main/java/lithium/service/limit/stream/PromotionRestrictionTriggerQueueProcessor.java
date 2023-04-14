package lithium.service.limit.stream;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response;
import lithium.service.casino.client.SportsBookClient;
import lithium.service.casino.client.objects.BonusRestrictionRequest;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.limit.client.objects.PromotionRestrictionTriggerData;
import lithium.service.limit.config.PromotionRestrictionRabbitConfiguration;
import lithium.service.limit.config.ServiceLimitConfigurationProperties;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.repositories.DomainRestrictionSetRepository;
import lithium.service.limit.services.UserRestrictionService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.stream.IDeadLetterQueueHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@Data
public class PromotionRestrictionTriggerQueueProcessor implements IDeadLetterQueueHandler {
    @Autowired
    private UserApiInternalClientService userApiInternalClientService;

    @Autowired
    LithiumServiceClientFactory lithiumServiceClientFactory;

    @Autowired
    UserRestrictionService userRestrictionService;

    @Autowired
    DomainRestrictionSetRepository domainRestrictionSetRepository;

    @Autowired
    ChangeLogService changeLogService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private final String parkingLotQueueName = PromotionRestrictionRabbitConfiguration.PARKING_LOT_QUEUE;

    @Autowired
    ServiceLimitConfigurationProperties configurationProperties;
    
    @Override
    @Bean
    @Qualifier("promotionRestrictionQueueParkingLot")
    public Queue parkingLotQueue() {
        return new Queue(PromotionRestrictionRabbitConfiguration.PARKING_LOT_QUEUE);
    }

    @RabbitListener(queues = PromotionRestrictionRabbitConfiguration.QUEUE,
            containerFactory = "promotionRestrictionRabbitListenerContainerFactory")
    public void trigger(Message msg, PromotionRestrictionTriggerData data,
                        @Header("x-retries") Integer retryCount, @Header("x-retries-total") Integer retryTotal) throws UserClientServiceFactoryException, Exception {
        log.debug("Received an promotions restriction trigger from the queue for processing: {}, message: {}", data,
                msg);


        User user = userApiInternalClientService.getUserByGuid(data.getUserGuid());

        BonusRestrictionRequest request = BonusRestrictionRequest.builder()
                .playerGuid(user.getGuid())
                .playerId(user.getId())
                .restricted(data.isRestrict())
                .retryTotal(retryTotal)
                .retryCount(retryCount)
                .build();

        if (canToggle(data)) {
            togglePromotionsAllowed(user, request.isRestricted());
            toggleSportsbookBonusRestriction(request, user);
        }
    }

    private void togglePromotionsAllowed(User user, boolean isRestricted) throws Exception {
        boolean promotionsOptOut = Optional.ofNullable(user.getPromotionsOptOut()).orElse(false);

        if (promotionsOptOut != isRestricted) { //Only call service-user promotions opt out if it is necessary

            try {
                userApiInternalClientService.setPromotionsOutOut(user.getId(), isRestricted);
            } catch (Exception e) {
                String lbError = "There was an issue toggling the Promotions Restriction within Lithium.";
                String lbErrorDetail = "";

                lbErrorDetail = String.format("Failed to update promotions opt out for player. userguid: %s playerId: %s, restrict: %s ",
                        user.getGuid(), user.getId(),isRestricted);

                List<ChangeLogFieldChange> cls = new ArrayList<>();
                cls.add(ChangeLogFieldChange.builder()
                        .field("error")
                        .toValue(lbErrorDetail)
                        .build()
                );

                changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), "default/system", null,
                        lbError, null, cls, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 1, user.getGuid().split("/")[0]);

                log.error(lbError + ", Data:" + lbErrorDetail);

                throw e; //Throwing this again here so that we can retry again using the DLQ
            }
        }
    }

    private void toggleSportsbookBonusRestriction(BonusRestrictionRequest request, User user) throws Exception {
        Response<?> response = sportsBookClient().toggleBonusRestriction(request, user.getDomain().getName());

        if (response.getStatus().id() != 200) {
            throw new Exception(response.getMessage());
        }
    }

    private SportsBookClient sportsBookClient() throws Exception {
        return lithiumServiceClientFactory.target(SportsBookClient.class);
    }

    public boolean canToggle(PromotionRestrictionTriggerData data) {
        boolean toggleBonus = true;
        if(data.getDomainRestrictionSetId() == null) {
            return toggleBonus;
        }
        DomainRestrictionSet set = domainRestrictionSetRepository.findOne(data.getDomainRestrictionSetId());

        // Only lift when there are no other comps block restrictions.
        if(!data.isRestrict() && set != null &&  userRestrictionService.isCompsRestriction(set)) {
            toggleBonus = userRestrictionService.getActiveCompsRestrictions(data.getUserGuid()).isEmpty();
        }

        return toggleBonus;
    }

    @Override
    @RabbitListener(queues = PromotionRestrictionRabbitConfiguration.DEAD_LETTER_QUEUE)
    public void dlqHandle(Message failedMessage) {
        Map<String, Object> headers = failedMessage.getMessageProperties().getHeaders();
        Integer retryCount = (Integer) headers.get("x-retries");
        if (retryCount == null) {
            retryCount = 0;
        }

        if (retryCount < getMaxDlqRetries()) {
            int messageDelay = (int)getMessageDelay(retryCount);
            log.debug("We will delay by {} seconds", messageDelay);
            headers.put("x-retries", retryCount + 1);
            headers.put("x-retries-total", getMaxDlqRetries());
            headers.put("x-delay", messageDelay);
            String exchange = (String) headers.get(RepublishMessageRecoverer.X_ORIGINAL_EXCHANGE);
            String originalRoutingKey = (String) headers.get(RepublishMessageRecoverer.X_ORIGINAL_ROUTING_KEY);

            log.warn("The retry count of {} is less than the maximum retry count of {}, we will retry this message. exchange {}, routing key", retryCount , getMaxDlqRetries(), exchange);

            getRabbitTemplate().convertAndSend(exchange, originalRoutingKey, failedMessage,
                    (message) -> {
                        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                        return message;
                    });
        } else {

            log.error("The retry count {} has reached the maximum amount of retries that have been configured, max retries: {}", retryCount, getMaxDlqRetries());
            getRabbitTemplate().convertAndSend(getParkingLotQueueName(), failedMessage,
                    (message) -> {
                        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return message;
                    });
        }
    }

    public double getMessageDelay(int numberOfFailures) {
        double initialInterval = configurationProperties.getQueues().getPromotionRestrictionTrigger().getBackOffOptionInterval();
        double backOffMultiplier = configurationProperties.getQueues().getPromotionRestrictionTrigger().getBackOffOptionMultiplier();
        double maxInterval = configurationProperties.getQueues().getPromotionRestrictionTrigger().getBackOffOptionMaxInterval();

        double currentDelay = initialInterval * Math.pow(backOffMultiplier, numberOfFailures);

        return Math.min(currentDelay, maxInterval);
    }

    public int getMaxDlqRetries() {
        return configurationProperties.getQueues().getPromotionRestrictionTrigger().getMaxDlqRetries();
    }
}
