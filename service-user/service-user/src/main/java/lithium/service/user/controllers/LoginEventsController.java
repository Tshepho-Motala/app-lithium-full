package lithium.service.user.controllers;

import java.util.ArrayList;
import java.util.List;

import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.user.client.objects.LoginEventFE;
import lithium.service.user.services.LoginEventService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.LoginEvent;
import lithium.service.user.data.repositories.DomainRepository;
import lithium.service.user.data.repositories.LoginEventRepository;
import lithium.service.user.data.specifications.LoginEventSpecification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/loginevents")
public class LoginEventsController {
	@Autowired DomainRepository domainRepository;
	@Autowired LoginEventRepository loginEventRepository;
	@Autowired
  CachingDomainClientService cachingDomainClientService;
	@Autowired LoginEventService loginEventService;
	
	@GetMapping(value="/table")
	private DataTableResponse<LoginEventFE> loginEventsTable(@RequestParam String domainNamesCommaSeperated, DataTableRequest request,
      LithiumTokenUtil tokenUtil) throws Exception {
		String[] domainNames = domainNamesCommaSeperated.split(",");
    DomainValidationUtil.filterDomainsWithRole(domainNames, "LOGINEVENTS_VIEW", tokenUtil);
		List<Domain> internalDomains = new ArrayList<>();
		List<lithium.service.domain.client.objects.Domain> externalDomains = new ArrayList<>();
		if (domainNames != null && domainNames.length > 0) {
			for (String domainName: domainNames) {
				internalDomains.add(domainRepository.findByName(domainName));
				externalDomains.add(cachingDomainClientService.retrieveDomainFromDomainService(domainName));
			}
		}
		if (internalDomains.size() > 0) {
			Specification<LoginEvent> spec = null;
			spec = Specification.where(LoginEventSpecification.domainIn(internalDomains));
			if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
				Specification<LoginEvent> s = Specification.where(LoginEventSpecification.anyContains(request.getSearchValue()));
				spec = (spec == null)? s: spec.and(s);
			}
			Page<LoginEvent> loginEvents = loginEventRepository.findAll(spec, request.getPageRequest());
			eventLoop:
			for (LoginEvent event: loginEvents) {
				for (lithium.service.domain.client.objects.Domain d: externalDomains) {
					if (event.getDomain()!=null) {
						if (event.getDomain().getName().equalsIgnoreCase(d.getName())) {
							if (d.getPlayers()) {
								event.setPlayerEvent(true);
							} else {
								event.setPlayerEvent(false);
							}
							continue eventLoop;
						}
					}
					if (event.getUser() == null) {
						event.setPlayerEvent(null);
						continue eventLoop;
					}
					if (event.getUser().getDomain().getName().equalsIgnoreCase(d.getName())) {
						if (d.getPlayers()) {
							event.setPlayerEvent(true);
						} else {
							event.setPlayerEvent(false);
						}
						continue eventLoop;
					}
				}
			}
			log.debug("loginEvents :: "+loginEvents.getContent());
			//return new DataTableResponse<LoginEvent>(request, loginEvents);
			return new DataTableResponse<>(request, loginEventService.mapLoginEventListToLoginEventFullList(loginEvents));
		} else {
			return new DataTableResponse<>(request, new ArrayList<LoginEventFE>());
		}
	}
}
