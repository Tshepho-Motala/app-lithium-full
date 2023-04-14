package lithium.service.mail.services;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.mail.client.internal.DoProviderRequest;
import lithium.service.mail.client.internal.DoProviderResponse;
import lithium.service.mail.client.internal.DoProviderResponseStatus;
import lithium.service.mail.config.ServiceMailConfigurationProperties;
import lithium.service.mail.data.entities.DomainProvider;
import lithium.service.mail.data.entities.Email;
import lithium.service.mail.data.repositories.EmailRepository;
import lithium.service.mail.services.provider.DoProvider;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class MailProcessService {
	@Autowired private AccessRuleService accessRuleService;
	@Autowired private CachingDomainClientService cachingDomainClientService;
	@Autowired private DoProvider doProvider;
	@Autowired private DomainProviderService domainProviderService;
	@Autowired private EmailRepository emailRepository;
	@Autowired private ServiceMailConfigurationProperties properties;
	@Autowired private UserApiInternalClientService userApiInternalClientService;

	private static final String NO_FROM_REPLACE = "noFromReplace";

	@TimeThisMethod
	public void process() {
		log.trace("EmailProcessingJob: processing mail");

		SW.start("mail.find");
		// Changed to look at oldest mail first.
		Page<Email> page = emailRepository.findByFailedFalseAndProcessingFalseAndSentDateIsNullAndErrorCountLessThanOrderByPriorityAscCreatedDateAsc(
				properties.getErrorThreshold(), PageRequest.of(0, properties.getProcessingJobPageSize()));
		SW.stop();
		List<Email> emails = page.getContent();
		log.trace("Page request of {} returned {} unsent mail. {} total in queue.",
				properties.getProcessingJobPageSize(), emails.size(), page.getTotalElements());

		Map<String, List<DomainProvider>> domainProvidersMap = new LinkedHashMap<>();

		SW.start("mail.process");
		emails.forEach(email -> {
			try {
				email.setProcessing(true);
				email.setProcessingStarted(new Date());
				email = emailRepository.save(email);

				String domainName = email.getDomain().getName();

				//We check the caching service disable smtp setting to decide whether to send email or not - it defaults to false
				boolean sendEmail = !cachingDomainClientService.disableSmtpSending(domainName);
				// FIXME: disable_smtp_sending - misleading property name as this not only blocks SMTP but any and
				//        all configured domain providers. (Yes, there is only an SMTP provider ATM.)
				//        This has a potential negative side effect.
				//        The processing state is left as true. This allows the queue to move along which is good.
				//        After lithium.services.mail.max-processing-mins has elapsed, processing flag will be set to
				//        false again, and processing will be re-attempted.
				//        I'm just refactoring... But think this might need more thought. We could end up with a large
				//        queue of mail that we need to retry processing over and over.

				//By default we want to send emails as system emails also use this system
				if (sendEmail) {
					List<DomainProvider> domainProviders = domainProvidersMap.computeIfAbsent(domainName, k -> {

						Iterable<DomainProvider> providers =  domainProviderService.findByDomainNameAndProviderType(
								domainName, ProviderConfig.ProviderType.DELIVERY.type());

						return StreamSupport.stream(providers.spliterator(), false)
								.collect(Collectors.toList());
					});

					DomainProvider domainProvider = null;

					if (domainProviders.isEmpty()) {
						log.warn("No domain provider setup! Using default SMTP settings from yml config.");
					} else {
						if (email.getUser() == null) {
							domainProvider = domainProviders.get(0);
						} else {
							domainProvider = chooseDomainProviderForUser(email.getUser().getGuid(), domainProviders);
						}
					}

					Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
					String defaultFrom = domain.getSupportEmail();

					DoProviderRequest request = DoProviderRequest.builder()
							.mailId(email.getId())
							.from(email.getFrom())
							.to(email.getTo())
							.subject(email.getSubject())
							.body(email.getBody())
							.attachmentName(email.getAttachmentName())
							.attachmentData(email.getAttachmentData())
							.build();

					boolean isFromOriginal = email.getFrom().equals(defaultFrom);
					if (!isFromOriginal) {
						Map<String, String> requestProps = new HashMap<>();
						requestProps.put(NO_FROM_REPLACE, "true");
						request.setProperties(requestProps);
					}

					// FIXME: This may take some time and we block the process queue until we get a response... On local,
					//        with the same SMTP provider setup on dev, it takes about 3 seconds. Possibly implement some
					//        kind of non blocking mechanism with a callback to svc-mail to update the state. That would
					//        speed things along nicely. But, i'm climbing out of this rabbit hole now...
					DoProviderResponse response = doProvider.run(domainProvider, request);

					if (response != null) {
						if (response.getStatus().equals(DoProviderResponseStatus.SUCCESS)) {
							email.setSentDate(new Date());
							if (isFromOriginal) {
								email.setFrom(response.getFrom());
							}
							email.setBcc(response.getBcc());
						} else {
							email.setErrorCount(email.getErrorCount() + 1);
							if (response.getMessage() != null && !response.getMessage().isEmpty())
								email.setLatestErrorReason(response.getMessage());
						}
					} else {
						email.setErrorCount(email.getErrorCount() + 1);
					}

					email.setProcessing(false);
					emailRepository.save(email);
				}
			} catch (Exception | UserClientServiceFactoryException e) {
				log.error(e.getMessage(), e);
				email.setProcessing(false);
				email.setErrorCount(email.getErrorCount() + 1);
				email.setLatestErrorReason(ExceptionUtils.getStackTrace(e));
				emailRepository.save(email);
			}
		});
		SW.stop();
	}

	private DomainProvider chooseDomainProviderForUser(String userGuid, List<DomainProvider> domainProviders)
			throws UserClientServiceFactoryException, UserNotFoundException {
		DomainProvider selectedDomainProvider = null;
		User user = null;

		for (int idx = 0; idx < domainProviders.size() && selectedDomainProvider == null; idx++) {
			DomainProvider domainProvider = domainProviders.get(idx);
			if (domainProvider.getAccessRule() != null) {
				if (user == null) user = userApiInternalClientService.getUserByGuid(userGuid);
				if (user.getLastLogin() == null) {
					// No IP/other necessary data, cannot check access rule, skip this domain provider.
					// Nb. IMPORTANT, flow changed!!
					// This previously just selected the first domain provider if user last login was null. It will now
					// traverse the list of providers and try to find one without an access rule, or, if none is found,
					// it will default to using the SMTP settings setup in the yml in service-mail-provider-smtp.
					continue;
				}
				boolean canUse = accessRuleService.checkAuthorization(domainProvider, user.getLastLogin().getIpAddress(),
						user.getLastLogin().getUserAgent());
				if (canUse) selectedDomainProvider = domainProvider;
			} else {
				selectedDomainProvider = domainProvider;
			}
		}

		if (selectedDomainProvider == null) {
			log.warn("No domain provider available for user {} after access rule filtering. Using default SMTP settings"
					+ " from yml config.", userGuid);
		}

		return selectedDomainProvider;
	}

	@TimeThisMethod
	public void processStuckMail() {
		Date threshold = new DateTime().minusMinutes(properties.getMaxProcessingMins()).toDate();
		log.trace("Processing stuck mail | maxProcessingMins: {}, threshold: {}", properties.getMaxProcessingMins(),
				threshold);
		// Most efficient way to do this.
		emailRepository.updateProcessingToFalseOnStuckMail(threshold);
	}
}
