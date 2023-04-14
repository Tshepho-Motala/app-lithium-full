package lithium.service.datafeed.provider.google.services;

import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.datafeed.provider.google.config.PubSubConfigService;
import lithium.service.datafeed.provider.google.config.PubSubGoogleProviderConfig;
import lithium.service.datafeed.provider.google.config.PublisherExecutorConfig;
import lithium.service.datafeed.provider.google.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.datafeed.provider.google.objects.DataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static com.google.api.gax.core.FixedCredentialsProvider.create;

@Service
@Slf4j
public class PublishersInitService {
    @Autowired
    private PubSubConfigService configService;
    @Autowired
    private PublisherExecutorConfig publisherExecutorConfig;

    public Publisher getPublisher(String domainName, DataType dataType) throws IOException, Status512ProviderNotConfiguredException {

        PubSubGoogleProviderConfig config = null;
        TopicName topicName = null;

        try {
            if (configService.isConfigNeedToUpdate(domainName)) {
                config = configService.buildAndStoreNewConfig(domainName);
            } else {
                config = configService.getActualConfig(domainName);
            }
            switch (dataType) {
                case ACCOUNT_CHANGES:
                    topicName = TopicName.of(config.getProject_id(), config.getUserChangeTopicKey());
                    break;
                case CASINO_TRANSACTIONS:
                    topicName = TopicName.of(config.getProject_id(), config.getCasinoTransactionsTopicKey());
                    break;
                case WALLET_TRANSACTIONS:
                    topicName = TopicName.of(config.getProject_id(), config.getWalletTransactionsTopicKey());
                    break;
                case ACCOUNT_LINK_CHANGES:
                    topicName = TopicName.of(config.getProject_id(), config.getAccountLinkFeedsTopicKey());
                    break;
                case VIRTUAL_TRANSACTIONS:
                    topicName = TopicName.of(config.getProject_id(), config.getVirtualTransactionsTopicKey());
                    break;
                case SPORTSBOOK_TRANSACTIONS:
                    topicName = TopicName.of(config.getProject_id(), config.getSportsbookBetChangeTopicKey());
                    break;
                case MARKETING_PREFERENCES:
                    topicName = TopicName.of(config.getProject_id(), config.getMarketingPreferencesTopicKey());
                    break;
                default:
                    log.error("Some of topic names don't set in google pub-sub provider domain settings ");
                    throw new Status512ProviderNotConfiguredException("Some of topic names don't present");
            }
        } catch (Status500InternalServerErrorException e) {
            log.error(e.getMessage());
        } catch (Status512ProviderNotConfiguredException e) {
            log.warn(e.getMessage());
        }

        if (config != null && topicName != null) {
            return initPublisher(config, topicName);
        } else {
            log.warn("Pub-sub provider is not correctly configured for domain " + domainName + " and channel" + dataType.getChannelName());
            throw new Status512ProviderNotConfiguredException(dataType.getChannelName());
        }
    }

    private Publisher initPublisher(PubSubGoogleProviderConfig config, TopicName topicName) throws IOException {
        Collection<String> collection = new ArrayList<>();
        String privateKey = config.getPrivate_key().replace("\\n", "\n");
        ServiceAccountCredentials serviceAccountCredentials = ServiceAccountCredentials.fromPkcs8(config.getClient_id(), config.getClient_email(), privateKey, config.getPrivate_key_id(), collection);
        FixedCredentialsProvider credentialsProvider = create(serviceAccountCredentials);
        ExecutorProvider executorProvider =
                InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(publisherExecutorConfig.getExecutorThreadCount()).build();
        return Publisher.newBuilder(topicName).setExecutorProvider(executorProvider).setCredentialsProvider(credentialsProvider).build();
    }
}
