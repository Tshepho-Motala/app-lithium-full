package lithium.service.pushmsg.services;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.pushmsg.client.internal.DoProviderClient;
import lithium.service.pushmsg.client.internal.DoProviderRequest;
import lithium.service.pushmsg.client.internal.DoProviderResponse;
import lithium.service.pushmsg.client.objects.PushMsgBasic;
import lithium.service.pushmsg.data.entities.Domain;
import lithium.service.pushmsg.data.entities.DomainProvider;
import lithium.service.pushmsg.data.entities.DomainProviderProperty;
import lithium.service.pushmsg.data.entities.ExternalUser;
import lithium.service.pushmsg.data.entities.PushMsg;
import lithium.service.pushmsg.data.entities.PushMsgTemplate;
import lithium.service.pushmsg.data.entities.User;
import lithium.service.pushmsg.data.repositories.ExternalUserRepository;
import lithium.service.pushmsg.data.repositories.PushMsgRepository;
import lithium.service.pushmsg.data.specifications.PushMsgSpecification;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PushMsgService {
	@Autowired PushMsgRepository pushMsgRepository;
	@Autowired PushMsgTemplateService pushMsgTemplateService;
	@Autowired UserService userService;
	@Autowired DomainService domainService;
	@Autowired TokenStore tokenStore;
	@Autowired DomainProviderService domainProviderService;
	@Autowired LithiumServiceClientFactory serviceFactory;
	@Autowired ExternalUserRepository externalUserRepository;
	
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy");
	
	public String replacePlaceholders(String text, Map<String, String> placeholders) {
		if (placeholders != null && text != null && !text.isEmpty()) {
			for (String placeholder: placeholders.keySet()) {
				text = text.replace(placeholder, placeholders.get(placeholder));
			}
		}
		return text;
	}
	
	@Async
	public void push(PushMsgBasic pmb) {
		if ((pmb.getTemplateId()!= null) && (!pmb.getTemplateId().isEmpty())) {
			log.info("Sending push msg :: "+pmb);
			pushFromTemplate(pmb);
		} else {
			//TODO: Implement ability to create notifications without predefined templates on provider side..
		}
	}
	
	private void pushFromTemplate(PushMsgBasic pmb) {
		Date createdDate = new Date();
		Domain domain = domainService.findOrCreate(pmb.getDomainName());
		List<DomainProvider> domainProviders = domainProviderService.findAll(domain.getName());
		
		List<String> uuids = new ArrayList<>();
		for (String guid:pmb.getUserGuids()) {
			User user = userService.findOrCreate(guid);
			if (!user.optOut()) {
				List<ExternalUser> externalUsers = externalUserRepository.findByUserGuid(guid);
				for (ExternalUser eu:externalUsers) {
					uuids.add(eu.getUuid());
				}
			}
		}
		
		for (DomainProvider domainProvider:domainProviders) {
			log.debug("DomainProvider : "+domainProvider);
			if (domainProvider.getEnabled()==false) continue;
			DoProviderClient client = null;
			try {
				client = serviceFactory.target(DoProviderClient.class,
					domainProvider.getProvider().getUrl(),
					true
				);
				Map<String, String> properties = new HashMap<>();
				for (DomainProviderProperty prop: domainProviderService.propertiesWithDefaults(domainProvider.getId())) {
					properties.put(prop.getProviderProperty().getName(), prop.getValue());
				}
				
				PushMsgTemplate pmt = pushMsgTemplateService.findByDomainNameAndName(domain.getName(), pmb.getTemplateId());
				
				PushMsg pm = PushMsg.builder()
				.createdDate(createdDate)
				.priority(pmb.getPriority())
				.templateId(pmb.getTemplateId())
				.users(userService.usersFromGuid(pmb.getUserGuids()))
				.domain(domain)
				.domainProvider(domainProvider)
				.build();
				
				DoProviderResponse dpr = client.send(
					DoProviderRequest.builder()
					.templateId(pmt.getCurrent().getProviderTemplateId())
					.includePlayerIds(uuids)
					.placeholders(pmb.getPlaceholders())
					.properties(properties)
					.build()
				);
				
				pm.setSentDate(new Date());
				pm.setProviderReference(dpr.getProviderId());
				pm.setFailed(dpr.getFailed());
				
				pushMsgRepository.save(pm);
			} catch (LithiumServiceClientFactoryException e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	public Page<PushMsg> findByDomain(String domainName, String searchValue, Pageable pageable) {
		Domain domain = domainService.findOrCreate(domainName);
		Specification<PushMsg> spec = Specification.where(PushMsgSpecification.domainIs(domain));
		Specification<PushMsg> s = Specification.where(PushMsgSpecification.any(searchValue));
		spec = (spec == null)? s: spec.and(s);
		
		return pushMsgRepository.findAll(spec, pageable);
	}
	
	public Page<PushMsg> findByDomain(String domainNamesCommaSeparated, boolean showSent, String createdDateStartString, String createdDateEndString, String searchValue, Pageable pageable, Principal principal) {
		List<lithium.service.pushmsg.data.entities.Domain> domains = new ArrayList<lithium.service.pushmsg.data.entities.Domain>();
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
		for (String domainName: util.domains()) {
			domains.add(domainService.findOrCreate(domainName));
		}
		
		if (domainNamesCommaSeparated != null) {
			String[] domainNames = domainNamesCommaSeparated.split(",");
			Iterator<lithium.service.pushmsg.data.entities.Domain> iterator = domains.iterator();
			while (iterator.hasNext()) {
				lithium.service.pushmsg.data.entities.Domain domain = iterator.next();
				boolean found = false;
				for (String domainName: domainNames) {
					if (domain.getName().equalsIgnoreCase(domainName)) { 
						found = true;
						break;
					}
				}
				if (!found) iterator.remove();
			}
		}
		
		Specification<PushMsg> spec = Specification.where(PushMsgSpecification.domainIn(domains));
		spec = spec.and(Specification.where(PushMsgSpecification.failedFalse()));
		if (!showSent) spec = spec.and(Specification.where(PushMsgSpecification.sentDateIsNull()));
		if (createdDateStartString != null && !createdDateStartString.isEmpty()) {
			DateTime createdDateStart = DATE_FORMATTER.parseDateTime(createdDateStartString);
			spec = spec.and(Specification.where(PushMsgSpecification.createdDateStart(createdDateStart.toDate())));
		}
		if (createdDateEndString != null && !createdDateEndString.isEmpty()) {
			DateTime createdDateEnd = DATE_FORMATTER.parseDateTime(createdDateEndString);
			createdDateEnd = createdDateEnd.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
			spec = spec.and(Specification.where(PushMsgSpecification.createdDateEnd(createdDateEnd.toDate())));
		}
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<PushMsg> s = Specification.where(PushMsgSpecification.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		
		return pushMsgRepository.findAll(spec, pageable);
	}
	
	public PushMsg findOne(Long id) throws LithiumServiceClientFactoryException {
		PushMsg pushMsg = pushMsgRepository.findOne(id);
//		if (pushMsg.getUsers() != null) {
//			UserApiInternalClient client = services.target(UserApiInternalClient.class, "service-user", true);
//			for (lithium.service.pushmsg.data.entities.User user:pushMsg.getUsers()) {
//				Response<User> response = client.getUser(user.getGuid());
//				if (response.isSuccessful()) {
//					pushMsg.setFullUser(response.getData());
//				}
//				
//			}
//		}
		return pushMsg;
	}
	
	public Page<PushMsg> findByUser(String userGuid, String searchValue, Pageable pageable) {
		Specification<PushMsg> spec = Specification.where(PushMsgSpecification.user(userService.findOrCreate(userGuid)));
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<PushMsg> s = Specification.where(PushMsgSpecification.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		return pushMsgRepository.findAll(spec, pageable);
	}
}