package lithium.service.access.provider.transunion.jobs;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.leader.LeaderCandidate;
import lithium.service.Response;
import lithium.service.access.provider.transunion.KycTransUnionModuleInfo;
import lithium.service.access.provider.transunion.exeptions.Status512ProviderNotConfiguredException;
import lithium.service.access.provider.transunion.service.TransUnionService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.domain.client.objects.ProviderType;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class PasswordUpdateJob {
    private final static String TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    @Autowired
    private LithiumServiceClientFactory services;
    @Autowired
    private LeaderCandidate leaderCandidate;
    @Autowired
    private KycTransUnionModuleInfo moduleInfo;
    @Autowired
    private TransUnionService transUnionService;

    @Scheduled(cron = "${lithium.service.access.gbg.jobs.password-update.cron:0 0 5 * * *}")
    public void process() throws Exception {
        log.debug("PasswordUpdateJob running");
        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return;
        }

        Response<Iterable<Provider>> dataResponse = getProviderService().listAllProvidersByTypeAndUrl(ProviderType.PROVIDER_TYPE_ACCESS, moduleInfo.getModuleName());
        for (Provider provider : dataResponse.getData()) {

            List<ProviderProperty> properties = provider.getProperties();

            boolean autoUpdateEnabled = properties.stream()
                    .filter(providerProperty -> providerProperty.getName().equalsIgnoreCase(KycTransUnionModuleInfo.ConfigProperties.PASSWORD_AUTO_UPDATE.getName()))
                    .map(ProviderProperty::getValue)
                    .anyMatch(value -> value.equalsIgnoreCase(String.valueOf(true)));

            if (autoUpdateEnabled) {

                DateTime now = new DateTime(new Date());

                ProviderProperty lastUpdatedProperty = properties.stream()
                        .filter(providerProperty -> providerProperty.getName().equalsIgnoreCase(KycTransUnionModuleInfo.ConfigProperties.PASSWORD_LAST_UPDATE_DATE.getName()))
                        .findFirst().orElseThrow(() -> new Status512ProviderNotConfiguredException("" + moduleInfo.getModuleName() + "/" + provider.getDomain().getName()));

                ProviderProperty delayProperty = properties.stream()
                        .filter(providerProperty -> providerProperty.getName().equalsIgnoreCase(KycTransUnionModuleInfo.ConfigProperties.PASSWORD_UPDATE_DELAY.getName()))
                        .findFirst().orElseThrow(() -> new Status512ProviderNotConfiguredException(moduleInfo.getModuleName() + "/" + provider.getDomain().getName()));

                if (lastUpdatedProperty.getValue().isEmpty()) {
                    getProviderService().updateProviderProperty(lastUpdatedProperty.getId(), now.toString(TIME_PATTERN));
                    return;
                }
                DateFormat formatter = new SimpleDateFormat(TIME_PATTERN);
                Date date = (Date) formatter.parse(lastUpdatedProperty.getValue());
                DateTime lastUpdateDate = new DateTime(date);

                int delayInDays = Integer.parseInt(delayProperty.getValue());

                if (lastUpdateDate.plusDays(delayInDays).isBeforeNow()) {
                    try {
                        String newPassword = transUnionService.updatePassword(User.SYSTEM_GUID, provider.getDomain());
                        ProviderProperty passwordProperty = properties.stream()
                                .filter(providerProperty -> providerProperty.getName().equalsIgnoreCase(KycTransUnionModuleInfo.ConfigProperties.PASSWORD.getName()))
                                .findFirst().orElseThrow(() -> new Status512ProviderNotConfiguredException(moduleInfo.getModuleName() + "/" + provider.getDomain().getName()));
                        getProviderService().updateProviderProperty(passwordProperty.getId(), newPassword);
                        getProviderService().updateProviderProperty(lastUpdatedProperty.getId(), now.toString(TIME_PATTERN));
                        log.info("Password for TransUnion successfully updated for domain:" + provider.getDomain().getName());
                    } catch (Status500InternalServerErrorException | Status512ProviderNotConfiguredException | IOException e) {
                        log.error("Can't update password for TransUnion KYC service for domain:" + provider.getDomain().getName());
                        throw e;
                    }
                }
            }
        }


    }

    private ProviderClient getProviderService() throws Status500InternalServerErrorException {
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
