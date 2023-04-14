package lithium.service.limit.controllers.backoffice;

import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.limit.data.entities.AutoRestrictionRule;
import lithium.service.limit.data.entities.AutoRestrictionRuleSet;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.services.AutoRestrictionRulesetService;
import lithium.service.limit.services.RestrictionService;
import lithium.util.DomainValidationUtil;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backoffice/auto-restriction/ruleset/{domainName}")
@Slf4j
public class BackofficeAutoRestrictionRuleSetController {
	@Autowired private AutoRestrictionRulesetService service;
	@Autowired private RestrictionService restrictionService;

	@PostMapping("/create")
	public Response<AutoRestrictionRuleSet> create(@PathVariable("domainName") String domainName,
												   @RequestBody AutoRestrictionRuleSet ruleset,
												   LithiumTokenUtil tokenUtil) {
		log.debug("BackofficeAutoRestrictionRuleSetController.create [ruleset="+ruleset+"]");
		try {
			ruleset = service.create(domainName, ruleset, tokenUtil.guid());
			return Response.<AutoRestrictionRuleSet>builder().data(ruleset).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to create ruleset [ruleset="+ruleset+"] " + e.getMessage(), e);
			return Response.<AutoRestrictionRuleSet>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}

	@PostMapping("/{id}/changename")
	public Response<AutoRestrictionRuleSet> changeName(@PathVariable("domainName") String domainName,
													   @PathVariable("id") Long id,
													   @RequestParam("newName") String newName,
													   LithiumTokenUtil tokenUtil)
			throws Status500InternalServerErrorException {
		log.debug("BackofficeAutoRestrictionRuleSetController.changeName [id="+id+", newName="+newName+"]");
		try {
			AutoRestrictionRuleSet ruleset = service.find(id);
			DomainValidationUtil.validate(ruleset.getDomain().getName(), domainName);
			ruleset = service.changeName(ruleset, newName, tokenUtil.guid());
			return Response.<AutoRestrictionRuleSet>builder().data(ruleset).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to change ruleset name [id="+id+", newName="+newName+"] " + e.getMessage(), e);
			return Response.<AutoRestrictionRuleSet>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}

	@PostMapping("/{id}/changeoutcome")
	public Response<AutoRestrictionRuleSet> changeOutcome(@PathVariable("domainName") String domainName,
														  @PathVariable("id") Long id,
														  @RequestParam("newOutcome") Integer newOutcome,
														  LithiumTokenUtil tokenUtil)
			throws Status500InternalServerErrorException {
		log.debug("BackofficeAutoRestrictionRuleSetController.changeOutcome [id="+id+", newOutcome="+newOutcome+"]");
		try {
			AutoRestrictionRuleSet ruleset = service.find(id);
			DomainValidationUtil.validate(ruleset.getDomain().getName(), domainName);
			ruleset = service.changeOutcome(ruleset, newOutcome, tokenUtil.guid());
			return Response.<AutoRestrictionRuleSet>builder().data(ruleset).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to change ruleset outcome [id="+id+", newOutcome="+newOutcome+"] " + e.getMessage(), e);
			return Response.<AutoRestrictionRuleSet>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}

	@PostMapping("/{id}/set-skip-test-user")
	public Response<AutoRestrictionRuleSet> setSkipTestUser(@PathVariable("domainName") String domainName,
														  @PathVariable("id") Long id,
														  @RequestParam("newSkipTestUser") boolean newSkipTestUser,
														  LithiumTokenUtil tokenUtil)
			throws Status500InternalServerErrorException {
		log.debug("BackofficeAutoRestrictionRuleSetController.newSkipTestUser [id="+id+", newSkipTestUser="+newSkipTestUser+"]");
		try {
			AutoRestrictionRuleSet ruleset = service.find(id);
			DomainValidationUtil.validate(ruleset.getDomain().getName(), domainName);
			if (ruleset.isDeleted()) {
				throw new Status500InternalServerErrorException("Ruleset not found");
			}
			ruleset = service.applySkipTestUser(ruleset, newSkipTestUser, tokenUtil.guid());
			return Response.<AutoRestrictionRuleSet>builder().data(ruleset).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to change ruleset skip test user [id="+id+", newSkipTestUser="+newSkipTestUser+"] " + e.getMessage(), e);
			return Response.<AutoRestrictionRuleSet>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}

	@PostMapping("/{id}/changerestriction")
	public Response<AutoRestrictionRuleSet> changeRestriction(@PathVariable("domainName") String domainName,
															  @PathVariable("id") Long id,
															  @RequestParam("newRestrictionId") Long newRestrictionId,
															  LithiumTokenUtil tokenUtil)
			throws Status500InternalServerErrorException {
		log.debug("BackofficeAutoRestrictionRuleSetController.changeRestriction [id="+id
			+ ", newRestrictionId="+newRestrictionId+"]");
		try {
			AutoRestrictionRuleSet ruleset = service.find(id);
			DomainValidationUtil.validate(ruleset.getDomain().getName(), domainName);
			DomainRestrictionSet domainRestrictionSet = restrictionService.find(newRestrictionId);
			DomainValidationUtil.validate(domainRestrictionSet.getDomain().getName(), domainName);
			ruleset = service.changeRestriction(ruleset, domainRestrictionSet, tokenUtil.guid());
			return Response.<AutoRestrictionRuleSet>builder().data(ruleset).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to change ruleset restriction [id="+id+", newRestrictionId="+newRestrictionId+"] "
				+ e.getMessage(), e);
			return Response.<AutoRestrictionRuleSet>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}

	@PostMapping("/{id}/delete")
	public Response<AutoRestrictionRuleSet> delete(@PathVariable("domainName") String domainName,
												   @PathVariable("id") Long id, LithiumTokenUtil tokenUtil)
			throws Status500InternalServerErrorException {
		log.debug("BackofficeAutoRestrictionRuleSetController.delete [id="+id+"]");
		try {
			AutoRestrictionRuleSet ruleset = service.find(id);
			DomainValidationUtil.validate(ruleset.getDomain().getName(), domainName);
			ruleset = service.deleteRuleset(ruleset, tokenUtil.guid());
			return Response.<AutoRestrictionRuleSet>builder().data(ruleset).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to delete ruleset [id="+id+"] " + e.getMessage(), e);
			return Response.<AutoRestrictionRuleSet>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}

	@PostMapping("/{id}/toggle/enabled")
	public Response<AutoRestrictionRuleSet> toggleEnabled(@PathVariable("domainName") String domainName,
														  @PathVariable("id") Long id, LithiumTokenUtil tokenUtil) {
		log.debug("BackofficeAutoRestrictionRuleSetController.toggleEnabled [id="+id+"]");
		try {
			AutoRestrictionRuleSet ruleset = service.find(id);
			DomainValidationUtil.validate(ruleset.getDomain().getName(), domainName);
			ruleset = service.toggleEnabled(ruleset, tokenUtil.guid());
			return Response.<AutoRestrictionRuleSet>builder().data(ruleset).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to toggle enabled flag on ruleset [id="+id+"] " + e.getMessage(), e);
			return Response.<AutoRestrictionRuleSet>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}

	@PostMapping("/{id}/rule/add")
	public Response<AutoRestrictionRuleSet> addRule(@PathVariable("domainName") String domainName,
													@PathVariable("id") Long id, @RequestBody AutoRestrictionRule rule,
													LithiumTokenUtil tokenUtil) {
		log.debug("BackofficeAutoRestrictionRuleSetController.addRule [id="+id+", rule="+rule+"]");
		try {
			AutoRestrictionRuleSet ruleset = service.find(id);
			DomainValidationUtil.validate(ruleset.getDomain().getName(), domainName);
			ruleset = service.addRule(ruleset, rule, tokenUtil.guid());
			return Response.<AutoRestrictionRuleSet>builder().data(ruleset).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to add rule to ruleset [id="+id+", rule="+rule+"] " + e.getMessage(), e);
			return Response.<AutoRestrictionRuleSet>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}

	@PostMapping("/{id}/rule/{ruleId}/update")
	public Response<AutoRestrictionRuleSet> updateRule(@PathVariable("domainName") String domainName,
													   @PathVariable("id") Long id, @PathVariable("ruleId") Long ruleId,
													   @RequestBody AutoRestrictionRule ruleUpdate,
													   LithiumTokenUtil tokenUtil) {
		log.debug("BackofficeAutoRestrictionRuleSetController.updateRule [id="+id+", ruleId="+ruleId+"]");
		try {
			AutoRestrictionRuleSet ruleset = service.find(id);
			DomainValidationUtil.validate(ruleset.getDomain().getName(), domainName);
			AutoRestrictionRule rule = service.findRule(ruleId);
			ruleset = service.updateRule(ruleset, rule, ruleUpdate, tokenUtil.guid());
			return Response.<AutoRestrictionRuleSet>builder().data(ruleset).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to update rule on ruleset [id="+id+", ruleId="+ruleId
				+", ruleUpdate="+ruleUpdate+"] " + e.getMessage(), e);
			return Response.<AutoRestrictionRuleSet>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}

	@PostMapping("/{id}/rule/{ruleId}/delete")
	public Response<AutoRestrictionRuleSet> deleteRule(@PathVariable("domainName") String domainName,
													   @PathVariable("id") Long id, @PathVariable("ruleId") Long ruleId,
													   LithiumTokenUtil tokenUtil) {
		log.debug("BackofficeAutoRestrictionRuleSetController.deleteRule [id="+id+", ruleId="+ruleId+"]");
		try {
			AutoRestrictionRuleSet ruleset = service.find(id);
			DomainValidationUtil.validate(ruleset.getDomain().getName(), domainName);
			AutoRestrictionRule rule = service.findRule(ruleId);
			ruleset = service.deleteRule(ruleset, rule, tokenUtil.guid());
			return Response.<AutoRestrictionRuleSet>builder().data(ruleset).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to delete rule on ruleset [id="+id+", ruleId="+ruleId+"] " + e.getMessage(), e);
			return Response.<AutoRestrictionRuleSet>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}

	@GetMapping("/{id}/changelogs")
	public @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable("domainName") String domainName,
														 @PathVariable Long id, @RequestParam int p) throws Exception {
		AutoRestrictionRuleSet ruleset = service.find(id);
		DomainValidationUtil.validate(ruleset.getDomain().getName(), domainName);
		return service.getChangeLogs(id, new String[] {"auto-restriction-ruleset", "auto-restriction-ruleset.rule"}, p);
	}
}
