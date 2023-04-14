package lithium.service.user.controllers;

import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.IncompleteUser;
import lithium.service.user.data.specifications.IncompleteUserSpecifications;
import lithium.service.user.services.IncompleteUserService;
import lithium.service.user.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/incompleteusers")
@Slf4j
public class IncompleteUsersController {
	@Autowired private IncompleteUserService incompleteUserService;
	@Autowired private UserService userService;

	@GetMapping("/table")
	public DataTableResponse<IncompleteUser> table(
		@RequestParam(name="domainNames", required=false) String[] domainNames,
		@RequestParam(name="categories", required=false) String[] categories,
		@RequestParam(name="loadUnfinished", required=false) boolean incompleteSignUps,
		@RequestParam(name="stage", required=false) String stage,
		DataTableRequest request,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		log.debug("Incomplete users table request " + request.toString());

		Specification<IncompleteUser> spec = null;

		List<Domain> domains = userService.filterRequestedDomainsForToken(domainNames, tokenUtil);

		if (domains.size() > 0) {
			spec = Specification.where(IncompleteUserSpecifications.domainIn(domains));
		} else {
			// No domains selected, returning empty list
			return new DataTableResponse<>(request, Collections.emptyList());
		}

		if (stage != null) {
			spec = spec.and(IncompleteUserSpecifications.stage(stage));
		}

		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<IncompleteUser> s = Specification.where(IncompleteUserSpecifications.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
		}

		Page<IncompleteUser> incompleteUsers = incompleteUserService.findIncompleteUsers(spec, request.getPageRequest());;

		return new DataTableResponse<>(request, incompleteUsers);
	}
}
