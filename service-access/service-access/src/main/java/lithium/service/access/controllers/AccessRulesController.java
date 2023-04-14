package lithium.service.access.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.INVALID_DATA;
import static lithium.service.Response.Status.NOT_FOUND;
import static lithium.service.Response.Status.OK;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response;
import lithium.service.access.client.objects.Action;
import lithium.service.access.client.objects.EAuthorizationOutcome;
import lithium.service.access.data.entities.AccessRule;
import lithium.service.access.data.entities.AccessRuleResultStatusOptions;
import lithium.service.access.data.repositories.AccessRuleRepository;
import lithium.service.access.data.repositories.AccessRuleStatusOptionsRepository;
import lithium.service.access.data.specifications.AccessRuleSpecifications;
import lithium.service.access.services.DomainService;
import lithium.service.access.services.ExternalDomainService;
import lithium.service.access.services.ListValueService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.objects.Domain;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/accessrules")
public class AccessRulesController {
	@Autowired AccessRuleRepository accessRuleRepository;
	@Autowired ExternalDomainService externalDomainService;
	@Autowired DomainService domainService;
	@Autowired ChangeLogService changeLogService;
	@Autowired ListValueService listValueService;
	@Autowired AccessRuleStatusOptionsRepository accessRuleStatusOptionsRepository;
	
	@GetMapping("/findByDomain/{domainName}")
	public Response<List<AccessRule>> domainAccessRules(@PathVariable("domainName") String domainName) {
		List<AccessRule> domainAccessRules = accessRuleRepository.findByDomainName(domainName);
		return Response.<List<AccessRule>>builder().data(domainAccessRules).status(OK).build();
	}
	
	@GetMapping("/findByValue")
	public Response<List<AccessRule>> findByValue(
		@RequestParam("domainName") String domainName,
		@RequestParam("listName") String listName,
		@RequestParam("accessRuleName") String accessRuleName,
		@RequestParam("value") String value,
    LithiumTokenUtil tokenUtil
	) {
	  try {
      DomainValidationUtil.validate(domainName, tokenUtil, "ACCESSRULES_VIEW", "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
      List<AccessRule> accessRules = accessRuleRepository.findAll(AccessRuleSpecifications.findValueInListInRuleInDomain(domainName, listName, accessRuleName, value));
      return Response.<List<AccessRule>>builder().data(accessRules).status(OK).build();
    } catch (Exception e) {
      return Response.<List<AccessRule>>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
	}
	
	@GetMapping("/addValueInListInRuleInDomain")
	public Response<List<AccessRule>> addValueInListInRuleInDomain(
		@RequestParam("domainName") String domainName,
		@RequestParam("listName") String listName,
		@RequestParam("accessRuleName") String accessRuleName,
		@RequestParam("value") String value,
    LithiumTokenUtil tokenUtil
	) {
		try {
      DomainValidationUtil.validate(domainName, tokenUtil, "ACCESSRULES_VIEW", "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
			List<AccessRule> accessRules = accessRuleRepository.findAll(AccessRuleSpecifications.findByListInRuleInDomain(domainName, listName, accessRuleName));
			if ((accessRules!=null) && (!accessRules.isEmpty())) {
				lithium.service.access.data.entities.List list = accessRules.stream()
				.filter(ar -> accessRuleName.equals(ar.getName()))
				.findFirst()
				.orElseThrow(Exception::new)
				.getAccessControlList()
				.stream()
				.filter(acl -> listName.equals(acl.getList().getName()))
				.findFirst()
				.orElseThrow(Exception::new)
				.getList();
				listValueService.addValue(list, value);
				return Response.<List<AccessRule>>builder().data(accessRuleRepository.findAll(AccessRuleSpecifications.findByListInRuleInDomain(domainName, listName, accessRuleName))).status(OK).build();
			}
		} catch (Exception e) {
			log.error("Could not add : '"+value+"' to : '"+listName+"' in : '"+accessRuleName+" for domain : "+domainName, e);
		}
		return Response.<List<AccessRule>>builder().status(INVALID_DATA).build();
	}
	
	@GetMapping("/table")
	public DataTableResponse<AccessRule> table(
		@RequestParam("domainNamesCommaSeperated") String domainNamesCommaSeperated,
		DataTableRequest request,
    LithiumTokenUtil tokenUtil
	) {
		String[] domainNames = domainNamesCommaSeperated.split(",");
		DomainValidationUtil.filterDomainsWithRoles(domainNames, tokenUtil, "ACCESSRULES_VIEW", "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
		if (domainNames.length > 0) {
			java.util.List<String> domainsList = Arrays.asList(domainNames);
			Specification<AccessRule> spec = Specification.where(AccessRuleSpecifications.domainIn(domainsList));
			if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
				Specification<AccessRule> s = Specification.where(AccessRuleSpecifications.anyContains(request.getSearchValue()));
				spec = (spec == null)? s: spec.and(s);
			}
			Page<AccessRule> accessRules = accessRuleRepository.findAll(spec, request.getPageRequest());
			return new DataTableResponse<>(request, accessRules);
		} else {
			return new DataTableResponse<>(request, new ArrayList<AccessRule>());
		}
	}
	
	@GetMapping("/{domainName}/{accessRuleName}")
	public Response<AccessRule> findByName(
		@PathVariable("domainName") String domainName,
		@PathVariable("accessRuleName") String accessRuleName,
    LithiumTokenUtil tokenUtil
	) {
	  try {
      AccessRule accessRule = accessRuleRepository.findByDomainNameAndNameIgnoreCase(domainName, accessRuleName);
      if (accessRule == null) {
        return Response.<AccessRule>builder().status(NOT_FOUND).build();
      }
      DomainValidationUtil.validate(domainName, tokenUtil, "ACCESSRULES_VIEW", "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
      return Response.<AccessRule>builder().data(accessRule).status(OK).build();
    } catch (Exception e) {
      return Response.<AccessRule>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
	}
	
	@PostMapping("/{domainName}/create/{accessRuleName}")
	public Response<AccessRule> create(
		@PathVariable("domainName") String domainName,
		@PathVariable("accessRuleName") String accessRuleName,
		LithiumTokenUtil tokenUtil
	) throws Exception {
	  try {
	    DomainValidationUtil.validate(domainName, tokenUtil, "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
      Domain externalDomain = externalDomainService.findByName(domainName);
      lithium.service.access.data.entities.Domain domain = domainService.findOrCreate(externalDomain.getName());
      AccessRule accessRule = accessRuleRepository.save(
          AccessRule.builder()
              .domain(domain)
              .name(accessRuleName)
              .defaultAction(Action.ACCEPT)
              .enabled(true)
              .build()
      );
      java.util.List<ChangeLogFieldChange> clfc = changeLogService.copy(accessRule, new AccessRule(), new String[] { "domain", "name", "defaultAction", "defaultMessage", "enabled" });
      changeLogService.registerChangesWithDomain("accessrule", "create", accessRule.getId(), tokenUtil.guid(), null, null, clfc, Category.ACCESS,
          SubCategory.ACCESS_RULE, 0, domainName);
      return Response.<AccessRule>builder().data(accessRule).status(OK).build();
    } catch (Exception e) {
      return Response.<AccessRule>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
	}

	/**
	 * List of possible outcomes from a provider.
	 * @see EAuthorizationOutcome
	 * @return
	 */
	@RequestMapping("/getStatusOptionOutcomeList")
	public Response<java.util.List<AccessRuleResultStatusOptions>> getStatusOptionOutcomeList(){
		java.util.ArrayList<AccessRuleResultStatusOptions> statusOptions = (java.util.ArrayList<AccessRuleResultStatusOptions>) accessRuleStatusOptionsRepository.findByOutcome(true);
		return Response.<java.util.List<AccessRuleResultStatusOptions>>builder().data(statusOptions).build();
	}

	/**
	 * List of possible outputs that service access is able to send to a caller service.
	 * @return
	 */
	@RequestMapping("/getStatusOptionOutputList")
	public Response<java.util.List<AccessRuleResultStatusOptions>> getStatusOptionOutputList(){
		java.util.ArrayList<AccessRuleResultStatusOptions> statusOptions = (java.util.ArrayList<AccessRuleResultStatusOptions>)accessRuleStatusOptionsRepository.findByOutput(true);
		return Response.<java.util.List<AccessRuleResultStatusOptions>>builder().data(statusOptions).build();
	}

}
