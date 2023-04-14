package lithium.service.datafeed.provider.google.config;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.datafeed.provider.google.ServiceDataFeedProviderGoogleModuleInfo;
import lithium.service.datafeed.provider.google.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.datafeed.provider.google.objects.DataType;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PubSubConfigService {

    @Autowired
    private ServiceDataFeedProviderGoogleModuleInfo moduleInfo;

    @Autowired
    protected LithiumServiceClientFactory services;

    private ConcurrentHashMap<String, PubSubGoogleProviderConfig> configs;

    public boolean isConfigNeedToUpdate(String domainName) throws Status512ProviderNotConfiguredException, Status500InternalServerErrorException {
        if (configs == null) {
            configs = new ConcurrentHashMap<String, PubSubGoogleProviderConfig>();
            return true;
        }
        if (configs.containsKey(domainName)) {
            PubSubGoogleProviderConfig actualConfig = configs.get(domainName);
            PubSubGoogleProviderConfig supposedConfig = getProviderConfig(domainName);
            return !actualConfig.equals(supposedConfig) || actualConfig.getCreateDate().plusDays(1).isAfterNow();
        }
        return true;
    }

    public PubSubGoogleProviderConfig getActualConfig(String domainName) {
        return this.configs.get(domainName);
    }

    public PubSubGoogleProviderConfig buildAndStoreNewConfig(String domainName) throws Status512ProviderNotConfiguredException, Status500InternalServerErrorException {
        configs.remove(domainName);
        PubSubGoogleProviderConfig newConfig = getProviderConfig(domainName);
        newConfig.setCreateDate(DateTime.now());
        configs.put(domainName, newConfig);
        return newConfig;
    }

    @Cacheable(value = "lithium.service.datafeed.provider.google.provider-config", key = "#root.args[0]", unless = "#result.isEmpty()")
    public PubSubGoogleProviderConfig getProviderConfig(String domainName) throws Status512ProviderNotConfiguredException, Status500InternalServerErrorException {
        String moduleName = moduleInfo.getModuleName();

        ProviderClient providerClient = getProviderClient();

        Response<Iterable<ProviderProperty>> providerProperties =
                providerClient.propertiesByProviderUrlAndDomainName(moduleName, domainName);

        if (!providerProperties.isSuccessful() || providerProperties.getData() == null) {
            log.warn("can't get pub-sub domain properties for domain:"+ domainName);
            throw new Status512ProviderNotConfiguredException("Pub-Sub");
        }
        PubSubGoogleProviderConfig config = new PubSubGoogleProviderConfig();
        config.setCreateDate(DateTime.now());

        for (ProviderProperty providerProperty : providerProperties.getData()) {
            /**
             * Set provider credentials
             */
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.PROJECT_ID.getName()))
                config.setProject_id(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.CLIENT_ID.getName()))
                config.setClient_id(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.CLIENT_EMAIL.getName()))
                config.setClient_email(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.PRIVATE_KEY_ID.getName()))
                config.setPrivate_key_id(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.PRIVATE_KEY.getName()))
                config.setPrivate_key(providerProperty.getValue());
            /**
             * Set the topic name from provider setting
             */
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.ACCOUNT_CHANGES_TOPIC_NAME.getName()))
                config.setUserChangeTopicKey(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.WALLET_TRANSACTION_TOPIC_NAME.getName()))
                config.setWalletTransactionsTopicKey(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.VIRTUALS_TOPIC_NAME.getName()))
                config.setVirtualTransactionsTopicKey(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.CASINO_TOPIC_NAME.getName()))
                config.setCasinoTransactionsTopicKey(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.SPORTSBOOK_TOPIC_NAME.getName()))
                config.setSportsbookBetChangeTopicKey(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.ACCOUNT_LINK_TOPIC_NAME.getName()))
                config.setAccountLinkFeedsTopicKey(providerProperty.getValue());
            if(providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.MARKETING_PREFERENCES_TOPIC_NAME.getName()))
                config.setMarketingPreferencesTopicKey(providerProperty.getValue());

            /**
             * Set whether channel should be active from the provider setting
             */
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.ACCOUNT_CHANGE_CHANNEL_ACTIVE.getName()))
                config.setAccountChangesActive(Boolean.parseBoolean(providerProperty.getValue()));
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.WALLET_TRANSACTION_CHANNEL_ACTIVE.getName()))
                config.setWalletTransactionActive(Boolean.parseBoolean(providerProperty.getValue()));
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.VIRTUALS_CHANNEL_ACTIVE.getName()))
                config.setVirtualsFeedActive(Boolean.parseBoolean(providerProperty.getValue()));
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.CASINO_CHANNEL_ACTIVE.getName()))
                config.setCasinoFeedActive(Boolean.parseBoolean(providerProperty.getValue()));
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.SPORTSBOOK_CHANNEL_ACTIVE.getName()))
                config.setSpotsBookFeedActive(Boolean.parseBoolean(providerProperty.getValue()));
            if (providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.ACCOUNT_LINK_CHANNEL_ACTIVE.getName()))
                config.setAccountLinkFeedActive(Boolean.parseBoolean(providerProperty.getValue()));
            if(providerProperty.getName().equalsIgnoreCase(ServiceDataFeedProviderGoogleModuleInfo.ConfigProperties.MARKETING_PREFS_CHANNEL_ACTIVE.getName()))
                config.setMarketingPreferencesActive(Boolean.parseBoolean(providerProperty.getValue()));
        }

        /**
         * Check whether the credential fields are not null
         */
        if (ObjectUtils.isEmpty(config.getProject_id()) || ObjectUtils.isEmpty(config.getClient_id())
                || ObjectUtils.isEmpty(config.getClient_email()) || ObjectUtils.isEmpty(config.getPrivate_key_id())
                || ObjectUtils.isEmpty(config.getPrivate_key())) {
            log.error("Some of Pub-Sub provider credential configurations fields for domain " + domainName + " not properly configured");
            throw new Status512ProviderNotConfiguredException("Google Pub-Sub");
        }

        /**
         * Check whether topic names was provided, if not specified, only disable relevant channel
         * without impacting exsting channels.
         */
        if (ObjectUtils.isEmpty(config.getUserChangeTopicKey())) {
            config.setAccountChangesActive(false);
        }
        if (ObjectUtils.isEmpty(config.getWalletTransactionsTopicKey())) {
            config.setWalletTransactionActive(false);
        }
        if (ObjectUtils.isEmpty(config.getVirtualTransactionsTopicKey())) {
            config.setVirtualsFeedActive(false);
        }
        if (ObjectUtils.isEmpty(config.getUserChangeTopicKey())) {
            config.setCasinoFeedActive(false);
        }
        if (ObjectUtils.isEmpty(config.getSportsbookBetChangeTopicKey())) {
            config.setSpotsBookFeedActive(false);
        }
        if (ObjectUtils.isEmpty(config.getAccountLinkFeedsTopicKey())) {
            config.setAccountLinkFeedActive(false);
        }
        if (ObjectUtils.isEmpty(config.getMarketingPreferencesTopicKey())) {
            config.setMarketingPreferencesActive(false);
        }
        return config;
    }


    public boolean isChannelActivated(String domainName, String channelName) {

        if (!isProviderEnabled(domainName)) {
            return false;
        }
        try {
            if (isConfigNeedToUpdate(domainName)) {
                buildAndStoreNewConfig(domainName);
            }
        } catch (Status512ProviderNotConfiguredException | Status500InternalServerErrorException e) {
            log.warn("can't update pub-sub provider config" + e.getMessage());
            return false;
        }
        DataType dataType = DataType.valueOf(channelName);
        PubSubGoogleProviderConfig config = configs.get(domainName);
        if (config != null) {
            switch (dataType) {
                case ACCOUNT_CHANGES:
                    return config.isAccountChangesActive();
                case WALLET_TRANSACTIONS:
                    return config.isWalletTransactionActive();
                case CASINO_TRANSACTIONS:
                    return config.isCasinoFeedActive();
                case VIRTUAL_TRANSACTIONS:
                    return config.isVirtualsFeedActive();
                case SPORTSBOOK_TRANSACTIONS:
                    return config.isSpotsBookFeedActive();
                case ACCOUNT_LINK_CHANGES:
                    return config.isAccountLinkFeedActive();
                case MARKETING_PREFERENCES:
                    return config.isMarketingPreferencesActive();
                default:
                    return false;
            }
        }
        return false;
    }

    private boolean isProviderEnabled(String domainName) {
        String moduleName = moduleInfo.getModuleName();
        ProviderClient providerClient = null;
        try {
            providerClient = getProviderClient();
        } catch (Status500InternalServerErrorException e) {
            log.warn("can't get providerClient for pub sub service" + e.getMessage());
            return false;
        }
        if (providerClient == null) {
            log.warn("can't find providerClient for pub sub config service");
            return false;
        }
        Response<Provider> response = providerClient.findByUrlAndDomainName(moduleName, domainName);
        return response.isSuccessful() && response.getData().getEnabled();
    }

    private ProviderClient getProviderClient() throws Status500InternalServerErrorException {
        ProviderClient cl = null;
        try {
            cl = services.target(ProviderClient.class, "service-domain", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem getting provider properties: " + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            throw new Status500InternalServerErrorException("Can't get service-domain provider client");
        }
        return cl;
    }
}
