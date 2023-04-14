package lithium.service.limit.controllers.backoffice;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.limit.data.entities.AutoRestrictionRuleSet;
import lithium.service.limit.services.AutoRestrictionRulesetService;
import lithium.util.DomainValidationUtil;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@RestController
@RequestMapping("/backoffice/auto-restriction/rulesets")
@Slf4j
public class BackofficeAutoRestrictionRuleSetsController {
	@Autowired private AutoRestrictionRulesetService service;

	@GetMapping("/table")
	public DataTableResponse<AutoRestrictionRuleSet> table(
		@RequestParam(name="enabled", required=false) Boolean enabled,
		@RequestParam(value="name", required=false) String name,
		@RequestParam(value="lastUpdatedStart", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") Date lastUpdatedStart,
		@RequestParam(value="lastUpdatedEnd", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") Date lastUpdatedEnd,
		@RequestParam("domains") String[] domains,
		DataTableRequest request,
		LithiumTokenUtil tokenUtil
	) {
		log.debug("BackofficeAutoRestrictionRuleSetsController.table [domains="+ Arrays.toString(domains)+", enabled="+enabled
			+", name="+name+", lastUpdatedStart="+lastUpdatedStart+", lastUpdatedEnd="+lastUpdatedEnd
			+", request="+request+"]");
		DomainValidationUtil.filterDomainsWithRole(domains, "AUTORESTRICTION_RULESETS_VIEW", tokenUtil);
		if (domains.length == 0) return new DataTableResponse<>(request, new ArrayList<>());
		Page<AutoRestrictionRuleSet> table = service.find(domains, enabled, name, lastUpdatedStart, lastUpdatedEnd,
			request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}

	@GetMapping("/{id}")
	public Response<AutoRestrictionRuleSet> findById(@PathVariable("id") Long id, LithiumTokenUtil tokenUtil) {
		log.debug("BackofficeAutoRestrictionRuleSetsController.findById [id="+id+"]");
		try {
			AutoRestrictionRuleSet ruleset = service.find(id);
			DomainValidationUtil.validate(ruleset.getDomain().getName(), "AUTORESTRICTION_RULESETS_VIEW",
				tokenUtil);
			if (ruleset.isDeleted()) {
				throw new Status500InternalServerErrorException("Ruleset not found");
			}
			return Response.<AutoRestrictionRuleSet>builder().data(ruleset).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to find ruleset [id="+id+"] " + e.getMessage(), e);
			return Response.<AutoRestrictionRuleSet>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}
}
