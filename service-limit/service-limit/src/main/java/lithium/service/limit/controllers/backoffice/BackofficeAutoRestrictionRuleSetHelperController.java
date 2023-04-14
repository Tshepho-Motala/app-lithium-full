package lithium.service.limit.controllers.backoffice;

import lithium.service.Response;
import lithium.service.limit.objects.AutoRestrictionRuleField;
import lithium.service.limit.objects.AutoRestrictionRuleOperator;
import lithium.service.limit.objects.AutoRestrictionRuleSetOutcome;
import lithium.service.limit.objects.RestrictionEvent;
import lithium.service.limit.services.AutoRestrictionRulesetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/backoffice/auto-restriction/helper")
@Slf4j
public class BackofficeAutoRestrictionRuleSetHelperController {
	@Autowired private AutoRestrictionRulesetService service;

	@GetMapping("/rule/fields")
	public Response<List<AutoRestrictionRuleField>> ruleFields(Locale locale) {
		return Response.<List<AutoRestrictionRuleField>>builder().data(service.ruleFields(locale))
			.status(Response.Status.OK).build();
	}

	@GetMapping("/rule/operators")
	public Response<List<AutoRestrictionRuleOperator>> ruleOperators(Locale locale) {
		return Response.<List<AutoRestrictionRuleOperator>>builder().data(service.ruleOperators(locale))
			.status(Response.Status.OK).build();
	}

	@GetMapping("/outcomes")
	public Response<List<AutoRestrictionRuleSetOutcome>> outcomes(Locale locale) {
		return Response.<List<AutoRestrictionRuleSetOutcome>>builder().data(service.ruleSetOutcomes(locale))
			.status(Response.Status.OK).build();
	}

	@GetMapping("/events")
	public Response<List<RestrictionEvent>> events(Locale locale) {
		return Response.<List<RestrictionEvent>>builder().data(service.events(locale)).build();
	}
}
