package lithium.service.sms.services;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.sms.data.entities.SMS;
import lithium.service.sms.data.entities.SMSTemplate;
import lithium.service.sms.data.repositories.SMSRepository;
import lithium.service.sms.data.specifications.SMSSpecification;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.objects.User;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SMSService {
	@Autowired SMSRepository smsRepository;
	@Autowired SMSTemplateService smsTemplateService;
	@Autowired UserService userService;
	@Autowired DomainService domainService;
	@Autowired ExternalUserService externalUserService;
	@Autowired TokenStore tokenStore;
	@Autowired LithiumServiceClientFactory services;
	@Autowired DefaultSMSTemplateService defaultSMSTemplateService;
	
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy");
	
	public String replacePlaceholders(String text, Set<Placeholder> placeholders) {
		if (placeholders != null && text != null && !text.isEmpty()) {
			for (Placeholder placeholder: placeholders) {
				text = text.replace(placeholder.getKey(), placeholder.getValue());
			}
		}
		return text;
	}

	public SMS saveForPlayerWithText(String from, String userGuid, String text) throws Exception {
		log.debug("Received sms save request for (" + userGuid + ") with text (" + text + ")");
		User user = null;
		try {
			user = externalUserService.getExternalUser(userGuid);
		} catch (LithiumServiceClientFactoryException e) {
			String msg = "Problem getting user from svc-user: " + userGuid;
			log.error(msg + " | " + e.getMessage(), e);
			throw new Exception(msg);
		}
		if (user.getCellphoneNumber() == null || user.getCellphoneNumber().isEmpty())
			throw new Exception("User " + userGuid + " does not have a cellphone no. listed");
		return smsRepository.save(
			SMS.builder()
			.from(from)
			.domain(domainService.findOrCreate(user.getDomain().getName()))
			.priority(1)
			.to(user.getCellphoneNumber())
			.text(text)
			.user(userService.findOrCreate(userGuid))
			.build()
		);
	}
	
	public SMS save(boolean stream, String domainName, String smsTemplateName, String smsTemplateLang, String to, int priority, String userGuid, Set<Placeholder> placeholders) throws Exception {
		log.debug("Received sms save request: domainName (" + domainName + ") smsTemplateName (" + smsTemplateName + ") to (" + to + ") placeholders (" + placeholders + ")");
		SMSTemplate smsTemplate = smsTemplateService.findByDomainNameAndNameAndLang(domainName, smsTemplateName, smsTemplateLang);
		String text = "";
		String message = "SMS template for domain (" + domainName + ") with template name (" + smsTemplateName + ") and template language (" + smsTemplateLang + ") ";
		if (smsTemplate == null || smsTemplate.getEnabled() == false) {
			String reason = (smsTemplate == null)? "not found!": "is disabled!";
			message +=   " " + reason;
			
			lithium.service.sms.data.entities.DefaultSMSTemplate defaultSMSTemplate = defaultSMSTemplateService.findByName(smsTemplateName);
			
			if (defaultSMSTemplate != null) {
				log.info("Using default sms template: " + defaultSMSTemplate + " for " + userGuid);
				log.debug("Using default sms template: " + defaultSMSTemplate + " with placeholders: " + placeholders);
				text = replacePlaceholders(defaultSMSTemplate.getText(), placeholders);
			} else {
				if (stream) {
					log.info(message);
					return null;
				}
				throw new IllegalArgumentException(message);
			}
		} else {
			text = replacePlaceholders(smsTemplate.getCurrent().getText(), placeholders);
			if (text == null) {
				message += " has a null text field.";
				log.warn(message);
				return smsRepository.save(
						SMS.builder()
								.from("SYSTEM")
								.domain(domainService.findOrCreate(domainName))
								.priority(priority)
								.to(to)
								.text("")
								.user(userService.findOrCreate(userGuid))
								.errorCount(1)
								.failed(true)
								.latestErrorReason(message)
								.processing(false)
								.build());

			}
		}
		return smsRepository.save(SMS.builder().from("SYSTEM").domain(domainService.findOrCreate(domainName)).priority(priority).to(to).text(text).user(userService.findOrCreate(userGuid)).build());
	}
	
	public Page<SMS> findByDomain(String domainNamesCommaSeparated, boolean showSent, boolean showFailed, String createdDateStartString,
	                              String createdDateEndString, String searchValue, Pageable pageable, LithiumTokenUtil tokenUtil) {
		List<lithium.service.sms.data.entities.Domain> domains = new ArrayList<lithium.service.sms.data.entities.Domain>();
		List<String> tokenPlayerDomains = tokenUtil.playerDomainsWithRoles("SMS_QUEUE_VIEW", "PLAYER_SMS_HISTORY_VIEW")
				.stream()
				.map(jwtDomain -> jwtDomain.getName())
				.collect(Collectors.toList());
		for (String domainName: tokenPlayerDomains) {
			domains.add(domainService.findOrCreate(domainName));
		}

		if (StringUtils.hasText(domainNamesCommaSeparated)) {
			String[] domainNames = domainNamesCommaSeparated.split(",");
			Iterator<lithium.service.sms.data.entities.Domain> iterator = domains.iterator();
			while (iterator.hasNext()) {
				lithium.service.sms.data.entities.Domain domain = iterator.next();
				boolean found = false;
				for (String domainName : domainNames) {
					if (domain.getName().equalsIgnoreCase(domainName)) {
						found = true;
						break;
					}
				}
				if (!found) iterator.remove();
			}

			Specification<SMS> spec = Specification.where(SMSSpecification.domainIn(domains));
			if (!showFailed) spec = spec.and(Specification.where(SMSSpecification.failedFalse()));
			if (!showSent) spec = spec.and(Specification.where(SMSSpecification.sentDateIsNull()));
			if (createdDateStartString != null && !createdDateStartString.isEmpty()) {
				DateTime createdDateStart = DATE_FORMATTER.parseDateTime(createdDateStartString);
				spec = spec.and(Specification.where(SMSSpecification.createdDateStart(createdDateStart.toDate())));
			}
			if (createdDateEndString != null && !createdDateEndString.isEmpty()) {
				DateTime createdDateEnd = DATE_FORMATTER.parseDateTime(createdDateEndString);
				createdDateEnd = createdDateEnd.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
				spec = spec.and(Specification.where(SMSSpecification.createdDateEnd(createdDateEnd.toDate())));
			}
			if ((searchValue != null) && (searchValue.length() > 0)) {
				Specification<SMS> s = Specification.where(SMSSpecification.any(searchValue));
				spec = (spec == null) ? s : spec.and(s);
			}

			return smsRepository.findAll(spec, pageable);
		}
		return new SimplePageImpl<>(new ArrayList<>(), 0, 1, 0);
	}
	
	public SMS findOne(Long id) throws LithiumServiceClientFactoryException {
		SMS sms = smsRepository.findOne(id);
		if (sms.getUser() != null) {
			UserApiInternalClient client = services.target(UserApiInternalClient.class, "service-user", true);
			Response<User> response = client.getUser(sms.getUser().getGuid());
			if (response.isSuccessful()) {
				sms.setFullUser(response.getData());
			}
		}
		return sms;
	}
	
	public Page<SMS> findByUser(String userGuid, String searchValue, Pageable pageable) {
		Specification<SMS> spec = Specification.where(SMSSpecification.user(userService.findOrCreate(userGuid)));
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<SMS> s = Specification.where(SMSSpecification.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		return smsRepository.findAll(spec, pageable);
	}
}
