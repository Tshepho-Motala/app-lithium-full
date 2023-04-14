package lithium.service.access.controllers;

import lithium.service.Response;
import lithium.service.access.client.objects.AuthorizationRequest;
import lithium.service.access.client.objects.AuthorizationResult;
import lithium.service.access.data.repositories.AccessRuleRepository;
import lithium.service.access.data.repositories.ValueRepository;
import lithium.service.access.services.AccessRuleService;
import lithium.service.access.services.AuthorizationService;
import lithium.service.access.services.UserExternalListValidationService;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static lithium.service.Response.Status.OK;

@RestController
@RequestMapping("/authorization/{domainName}/{accessRuleName}")
@Slf4j
public class AuthorizationController {
	@Autowired AccessRuleRepository accessRuleRepository;
	@Autowired AuthorizationService authorizationService;
	@Autowired ValueRepository valueRepository;
	@Autowired LithiumServiceClientFactory services;
	@Autowired AccessRuleService accessRuleService;
	@Autowired UserExternalListValidationService userExternalListValidationService;

	private static final String ACTION_REJECT = "Reject";
	private static final String ACTION_ACCEPT = "Accept";
	private static final String ACTION_ALLOW = "Allow";
	
	@PostMapping("/checkAuthorization")
	public Response<AuthorizationResult> checkAuthorization(
		@PathVariable("domainName") String domainName,
		@PathVariable("accessRuleName") String accessRuleName,
		@RequestBody AuthorizationRequest authorizationRequest,
		@RequestParam(required = false, name = "test", defaultValue = "false") Boolean test
	) {
		AuthorizationResult authorizationResult = authorizationService.checkAuthorization(domainName, accessRuleName, authorizationRequest, test);
		return Response.<AuthorizationResult>builder()
			.data(authorizationResult)
			.status(OK)
			.build();
	}

	@PostMapping("/isAccessRuleEnabled")
	public Response<Boolean> isAccessRuleEnabled(
	    @PathVariable("domainName") String domainName,
      @PathVariable("accessRuleName") String accessRuleName,
      @RequestBody AuthorizationRequest authorizationRequest,
      @RequestParam(required = false, name = "test", defaultValue = "false") Boolean test) {
    return Response.<Boolean>builder()
        .data(authorizationService.isAccessRuleEnabledByDomainAndRulesetName(domainName, accessRuleName))
        .status(OK)
        .build();
	}
}
