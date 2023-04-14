package lithium.service.sms.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lithium.leader.LeaderCandidate;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lithium.metrics.LithiumMetricsService;
import lithium.service.sms.client.internal.DoProviderRequest;
import lithium.service.sms.client.internal.DoProviderResponse;
import lithium.service.sms.client.internal.DoProviderResponseStatus;
import lithium.service.sms.config.ServiceSMSConfigurationProperties;
import lithium.service.sms.data.entities.DomainProvider;
import lithium.service.sms.data.entities.SMS;
import lithium.service.sms.data.repositories.SMSRepository;
import lithium.service.sms.services.AccessRuleService;
import lithium.service.sms.services.DomainProviderService;
import lithium.service.sms.services.ExternalUserService;
import lithium.service.sms.services.provider.DoProvider;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SMSProcessingJob {
    @Autowired
    ServiceSMSConfigurationProperties properties;
    @Autowired
    SMSRepository smsRepository;
    @Autowired
    LithiumMetricsService metrics;
    @Autowired
    DomainProviderService domainProviderService;
    @Autowired
    DoProvider doProvider;
    @Autowired
    ExternalUserService userService;
    @Autowired
    AccessRuleService accessRuleService;
    @Autowired
    LeaderCandidate leaderCandidate;


    @Scheduled(cron = "${lithium.services.sms.processing-job-cron:*/1 * * * * *}")
    public void send() throws Exception {
        //Leadership
        if (!leaderCandidate.iAmTheLeader()) {
            log.debug("I am not the leader.");
            return;
        }
        log.debug("SMSProcessingJob: processing msgs started");
        sendRun();
        log.debug("SMSProcessingJob: processing msgs processing completed");
    }

    private void sendRun() throws Exception {
        metrics.timer(log).time("processingMsgs", (StopWatch sw) -> {

            sw.start("checkingForAndResolvingStuckSms");
            Page<SMS> stuckSms = smsRepository.findByFailedFalseAndProcessingTrueAndProcessingStartedLessThanOrderByPriorityAscCreatedDateDesc(
                    new DateTime().minusMinutes(properties.getMaxProcessingMins()).toDate(), PageRequest.of(0, properties.getProcessingJobPageSize()));
            stuckSms.getContent().forEach(sms -> {
                sms.setProcessing(false);
                sms = smsRepository.save(sms);
            });
            sw.stop();

            sw.start("retrieveListAndSetProcessing");
            Page<SMS> smsPage = smsRepository
                    .findByFailedFalseAndProcessingFalseAndSentDateIsNullAndErrorCountLessThanOrderByPriorityAscCreatedDateDesc(
                            properties.getSmsErrorCountLessThan(), PageRequest.of(0, properties.getProcessingJobPageSize()));
            List<SMS> smsList = smsPage.getContent();
            if (!smsList.isEmpty()) {
                log.info("Page request of (" + properties.getProcessingJobPageSize() + ") returned (" + smsList.size() + ") unsent msgs. ("
                        + smsPage.getTotalElements() + ") total in queue.");
            }
            List<SMS> sendList = new ArrayList<>();
            for (SMS sms : smsList) {
                try {
                    sms.setProcessingStarted(new Date());
                    sms.setProcessing(true);
                    sms = smsRepository.save(sms);
                    sendList.add(sms);
                } catch (Exception e) {
                    log.warn("SMS not added to send list (" + sms + ") due " + e.getMessage());
                }
            }
            sw.stop();

            sw.start("processRetrievedList");
            for (SMS sms : sendList) {
                sms = smsRepository.findOne(sms.getId());
                List<DomainProvider> domainProviders = domainProviderService.findAll(sms.getDomain().getName());
                domainProviders.stream()
                        .filter(dp -> {
                            return (!dp.getDeleted() && dp.getEnabled());
                        })
                        .sorted((dp1, dp2) -> dp2.getPriority().compareTo(dp1.getPriority()));
                DomainProvider domainProvider = null;
                if (domainProviders == null || domainProviders.size() < 1) {
                    sms.setFailed(true);
                    smsRepository.save(sms);
                    log.debug("No providers setup! Set Failed for SMS | " + sms);
                    return;
                } else {
                    if (sms.getUser() != null) {
                        try {
                            User user = userService.getExternalUser(sms.getUser().getGuid());
                            if (user.getLastLogin() != null) {
                                List<DomainProvider> filteredDomainProviders =
                                        domainProviders.stream().filter(dp -> {
                                            return accessRuleService.checkAuthorization(
                                                    dp, user.getLastLogin().getIpAddress(), user.getLastLogin().getUserAgent());
                                        })
                                                .collect(Collectors.toList());
                                if (filteredDomainProviders != null && !filteredDomainProviders.isEmpty()) {
                                    domainProvider = filteredDomainProviders.get(0);
                                } else {
                                    domainProvider = domainProviders.get(0);
                                }
                            } else {
                                domainProvider = domainProviders.get(0);
                            }
                        } catch (Exception e) {
                            log.warn("Could not filter by access rule. " + e.getMessage(), e);
                        }
                    } else {
                        domainProvider = domainProviders.get(0);
                    }
                }
                try {
                    List<String> to = new ArrayList<String>();
                    to.add(sms.getTo());
                    DoProviderResponse response = doProvider.run(domainProvider,
                            DoProviderRequest.builder().to(to).content(sms.getText()).smsId(sms.getId()).priority(sms.getPriority()).build());
                    sms.setSentDate(new Date());
                    if (response != null) {
                        if (response.getStatus().getCode().equals(DoProviderResponseStatus.SUCCESS.getCode()) ||
                                response.getStatus().getCode().equals(DoProviderResponseStatus.PENDING.getCode())) {
                            sms.setDomainProvider(domainProvider);
                            sms.setProviderReference(response.getProviderReference());
                        } else {
                            sms.setErrorCount(sms.getErrorCount() + 1);
                            sms.setLatestErrorReason(response.getMessage());
                        }
                    } else {
                        sms.setErrorCount(sms.getErrorCount() + 1);
                    }
                    sms.setProcessing(false);
                    smsRepository.save(sms);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    sms.setProcessing(false);
                    sms.setErrorCount(sms.getErrorCount() + 1);
                    sms.setLatestErrorReason(ExceptionUtils.getStackTrace(e));
                    smsRepository.save(sms);
                }
            }
            sw.stop();
        });
    }
}
