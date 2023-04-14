package lithium.service.user.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.client.objects.User;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.SignupEvent;
import lithium.service.user.data.repositories.DomainRepository;
import lithium.service.user.services.SignupEventService;
import lithium.service.user.services.SignupService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signupevents")
public class SignupEventsController {
	@Autowired DomainRepository domainRepository;
	@Autowired SignupEventService service;
	@Autowired SignupService signupService;
	
	@GetMapping(value="/table")
	private DataTableResponse<SignupEvent> signupEventsTable(
		@RequestParam String domainNamesCommaSeperated,
		@RequestParam(name="userId", required=false) Long userId,
		@RequestParam(name="signupDateRangeStart", required=false) String signupDateRangeStart,
		@RequestParam(name="signupDateRangeEnd", required=false) String signupDateRangeEnd,
		@RequestParam(name="successful", required=false) Boolean successful,
    @RequestParam("order[0][column]") String orderColumn,
    @RequestParam("order[0][dir]") String orderDirection,
		DataTableRequest request,
    LithiumTokenUtil tokenUtil
	) {
    Sort sort = request.getPageRequest().getSort();
    if (orderColumn.equals("7"))
      sort = Sort.by(Sort.Direction.fromString(orderDirection),"user.lastName");
    request.setPageRequest(PageRequest.of(request.getPageRequest().getPageNumber(),
        request.getPageRequest().getPageSize() > 100 ? 100 : request.getPageRequest().getPageSize(),
        sort));

		String[] domainNames = domainNamesCommaSeperated.split(",");
    DomainValidationUtil.filterDomainsWithRole(domainNames, "SIGNUPEVENTS_VIEW", tokenUtil);
		List<Domain> domains = new ArrayList<>();
		if (domainNames != null && domainNames.length > 0) {
			for (String domainName: domainNames) {
				Domain domain = domainRepository.findByName(domainName);
				if (domain != null) {
					domains.add(domain);
				}
			}
		}
		if (domains.size() > 0) {
			return new DataTableResponse<>(request, service.find(domains, userId, signupDateRangeStart, signupDateRangeEnd, successful, request.getSearchValue(), request.getPageRequest()));
		} else {
			return new DataTableResponse<>(request, new ArrayList<SignupEvent>());
		}
	}
	
	@PostMapping("/export")
	public List<User> exportByDateRange(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, LithiumTokenUtil tokenUtil) {
		List<String> domains = tokenUtil.playerDomainsWithRole("SIGNUPEVENTS_VIEW").stream().map(jwtDomain -> jwtDomain.getName()).collect(Collectors.toList());
	  return signupService.getSignupsForDateRange(domains, new DateTime(startDate), new DateTime(endDate));
	}
}
