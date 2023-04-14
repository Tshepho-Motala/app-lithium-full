//package lithium.service.pushmsg.jobs;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.apache.commons.lang.exception.ExceptionUtils;
//import org.joda.time.DateTime;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StopWatch;
//
//import lithium.metrics.LithiumMetricsService;
//import lithium.service.pushmsg.config.ServiceSMSConfigurationProperties;
//import lithium.service.pushmsg.data.entities.DomainProvider;
//import lithium.service.pushmsg.data.entities.SMS;
//import lithium.service.pushmsg.data.repositories.SMSRepository;
//import lithium.service.pushmsg.services.AccessRuleService;
//import lithium.service.pushmsg.services.DomainProviderService;
//import lithium.service.pushmsg.services.ExternalUserService;
//import lithium.service.pushmsg.services.provider.DoProvider;
//import lithium.service.sms.client.internal.DoProviderRequest;
//import lithium.service.sms.client.internal.DoProviderResponse;
//import lithium.service.sms.client.internal.DoProviderResponseStatus;
//import lithium.service.user.client.objects.User;
//import lombok.extern.slf4j.Slf4j;
//
//@Component
//@Slf4j
//public class SMSProcessingJob {
//	@Autowired ServiceSMSConfigurationProperties properties;
//	@Autowired SMSRepository pushMsgRepository;
//	@Autowired LithiumMetricsService metrics;
//	@Autowired DomainProviderService domainProviderService;
//	@Autowired DoProvider doProvider;
//	@Autowired ExternalUserService userService;
//	@Autowired AccessRuleService accessRuleService;
//	
//	@Scheduled(cron="${lithium.services.sms.processing-job-cron:*/1 * * * * *}")
//	public void send() throws Exception {
//		metrics.timer(log).time("processingMsgs", (StopWatch sw) -> {
//			log.debug("SMSProcessingJob: processing msgs");
//			
//			sw.start("checkingForAndResolvingStuckSms");
//			Page<SMS> stuckSms = pushMsgRepository.findByFailedFalseAndProcessingTrueAndProcessingStartedLessThanOrderByPriorityAscCreatedDateDesc(
//				new DateTime().minusMinutes(properties.getMaxProcessingMins()).toDate(), PageRequest.of(0, properties.getProcessingJobPageSize()));
//			stuckSms.getContent().forEach(sms -> {
//				sms.setProcessing(false);
//				sms = pushMsgRepository.save(sms);
//			});
//			sw.stop();
//
//			sw.start("retrieveListAndSetProcessing");
//			Page<SMS> smsPage = pushMsgRepository
//				.findByFailedFalseAndProcessingFalseAndSentDateIsNullAndErrorCountLessThanOrderByPriorityAscCreatedDateDesc(
//					properties.getSmsErrorCountLessThan(), PageRequest.of(0, properties.getProcessingJobPageSize()));
//			List<SMS> smsList = smsPage.getContent();
//			log.debug("Page request of (" + properties.getProcessingJobPageSize()  + ") returned (" + smsList.size() + ") unsent msgs. ("
//				+ smsPage.getTotalElements() + ") total in queue.");
//			List<SMS> sendList = new ArrayList<>();
//			smsList.parallelStream().forEach(sms -> {
//				try {
//					sms.setProcessing(true);
//					sms.setProcessingStarted(new Date());
//					pushMsgRepository.save(sms);
//					sendList.add(sms);
//				} catch (Exception e) {
//					log.warn("SMS not added to send list (" + sms + ")");
//				}
//			});
//			sw.stop();
//
//			sw.start("processRetrievedList");
//			sendList.parallelStream().forEach(sms -> {
//				sms = pushMsgRepository.findOne(sms.getId());
//				List<DomainProvider> domainProviders = domainProviderService.findAll(sms.getDomain().getName());
//				domainProviders.stream()
//				.filter(dp -> { return (!dp.getDeleted() && dp.getEnabled()); })
//				.sorted((dp1, dp2) -> dp2.getPriority().compareTo(dp1.getPriority()));
//				DomainProvider domainProvider = null;
//				if (domainProviders == null || domainProviders.size() < 1) {
//					log.warn("No providers setup! Skipping this SMS | " + sms);
//					return;
//				} else {
//					if (sms.getUser() != null) {
//						try {
//							User user = userService.getExternalUser(sms.getUser().getGuid());
//							if (user.getLastLogin() != null) {
//								List<DomainProvider> filteredDomainProviders =
//									domainProviders.stream().filter(dp -> {
//										return accessRuleService.checkAuthorization(
//												dp, user.getLastLogin().getIpAddress(), user.getLastLogin().getUserAgent());
//									})
//									.collect(Collectors.toList());
//								if (filteredDomainProviders != null && !filteredDomainProviders.isEmpty()) {
//									domainProvider = filteredDomainProviders.get(0);
//								} else {
//									domainProvider = domainProviders.get(0);
//								}
//							} else {
//								domainProvider = domainProviders.get(0);
//							}
//						} catch (Exception e) {
//							log.warn("Could not filter by access rule. " + e.getMessage(), e);
//						}
//					} else {
//						domainProvider = domainProviders.get(0);
//					}
//				}
//				try {
//					List<String> to = new ArrayList<String>();
//					to.add(sms.getTo());
//					DoProviderResponse response = doProvider.run(domainProvider,
//						DoProviderRequest.builder().to(to).content(sms.getText()).smsId(sms.getId()).priority(sms.getPriority()).build());
//					sms.setSentDate(new Date());
//					if (response != null) {
//						if (response.getStatus().getCode().equals(DoProviderResponseStatus.SUCCESS.getCode()) ||
//							response.getStatus().getCode().equals(DoProviderResponseStatus.PENDING.getCode())) {
//							sms.setDomainProvider(domainProvider);
//							sms.setProviderReference(response.getProviderReference());
//						} else {
//							sms.setErrorCount(sms.getErrorCount() + 1);
//							sms.setLatestErrorReason(response.getMessage());
//						}
//					} else {
//						sms.setErrorCount(sms.getErrorCount() + 1);
//					}
//					sms.setProcessing(false);
//					pushMsgRepository.save(sms);
//				} catch (Exception e) {
//					log.error(e.getMessage(), e);
//					sms.setProcessing(false);
//					sms.setErrorCount(sms.getErrorCount() + 1);
//					sms.setLatestErrorReason(ExceptionUtils.getStackTrace(e));
//					pushMsgRepository.save(sms);
//				}
//			});
//			sw.stop();
//		});
//	}
//}