package lithium.service.cashier.controllers.admin;

import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalInitData;
import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleSetDto;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalDtoConvertService;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.AutoWithdrawalRuleSet;
import lithium.service.cashier.data.entities.AutoWithdrawalRuleSetProcess;
import lithium.service.cashier.client.objects.autowithdrawal.AutoWithdrawalRuleField;
import lithium.service.cashier.data.objects.AutoWithdrawalRuleOperator;
import lithium.service.cashier.services.autowithdrawal.AutoWithdrawalInitDataService;
import lithium.service.cashier.services.AutoWithdrawalRulesetService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/admin/auto-withdrawal/ruleset")
@Slf4j
@AllArgsConstructor
public class AdminAutoWithdrawalRuleSetController {
	private final AutoWithdrawalRulesetService service;
	private final AutoWithdrawalInitDataService initDataService;
	private final AutoWithdrawalDtoConvertService convertService;

	@GetMapping("/rule/fields")
	public Response<List<AutoWithdrawalRuleField>> ruleFields(Locale locale) {
		return Response.<List<AutoWithdrawalRuleField>>builder().data(initDataService.ruleTypes(locale)).status(Response.Status.OK).build();
	}

	@GetMapping("/rule/operators")
	public Response<List<AutoWithdrawalRuleOperator>> ruleOperators(Locale locale) {
		return Response.<List<AutoWithdrawalRuleOperator>>builder().data(service.ruleOperators(locale)).status(Response.Status.OK).build();
	}

	@GetMapping("/rule/{domainName}/init-data/{ruleTypeId}")
	public Response<AutoWithdrawalInitData> initData(
		@PathVariable("domainName") String domainName,
		@PathVariable("ruleTypeId") AutoWithdrawalRuleType ruleType,
		Locale locale) {
		try {
			return Response.<AutoWithdrawalInitData>builder()
				.data(initDataService.getInitData(domainName, ruleType, locale))
				.status(Response.Status.OK)
				.build();
		} catch(Exception e) {
			log.error("Failed to get initialization data for [domains="+ domainName +", rule field=" + ruleType.name() + "] Exception:" + e.getMessage(), e);
			return Response.<AutoWithdrawalInitData>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}

	@GetMapping("/table")
	public DataTableResponse<AutoWithdrawalRuleSetDto> table(
		@RequestParam(name="enabled", required=false) Boolean enabled,
		@RequestParam(value="name", required=false) String name,
		@RequestParam(value="lastUpdatedStart", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") Date lastUpdatedStart,
		@RequestParam(value="lastUpdatedEnd", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") Date lastUpdatedEnd,
		@RequestParam("domains") String[] domains,
		DataTableRequest request
	) {
		log.debug("AdminAutoWithdrawalRuleSetController.table [domains=" + Arrays.toString(domains) + ", enabled=" + enabled
				+ ", name=" + name + ", lastUpdatedStart=" + lastUpdatedStart + ", lastUpdatedEnd=" + lastUpdatedEnd
				+ ", request=" + request + "]");
		if (domains.length == 0) {
			return new DataTableResponse<>(request, new ArrayList<>());
		}
		Page<AutoWithdrawalRuleSetDto> table = service.find(domains, enabled, name, lastUpdatedStart, lastUpdatedEnd,
			request.getSearchValue(), request.getPageRequest())
				.map(convertService::ruleSetToDto);

		return new DataTableResponse<>(request, table);
	}

	@GetMapping("/{id}")
	public Response<AutoWithdrawalRuleSetDto> findById(@PathVariable("id") AutoWithdrawalRuleSet ruleset,
	                                                LithiumTokenUtil tokenUtil) {
		log.debug("AdminAutoWithdrawalRuleSetController.findById [ruleset=" + ruleset + "]");
		try {
			DomainValidationUtil.validate(ruleset.getDomain().getName(), "AUTOWITHDRAWALS_RULESETS_VIEW", tokenUtil);
			if (ruleset.isDeleted()) {
				throw new Status500InternalServerErrorException("Ruleset not found");
			}
			return Response.<AutoWithdrawalRuleSetDto>builder()
					.data(convertService.ruleSetToDto(ruleset))
					.status(Response.Status.OK)
					.build();
		} catch (Exception e) {
			log.error("Failed to find ruleset [ruleset=" + ruleset + "] " + e.getMessage(), e);
			return failedRuleSetResponse(e);
		}
	}

	@PostMapping("/{domainName}/create")
	public Response<AutoWithdrawalRuleSetDto> create(@PathVariable("domainName") String domainName,
		@RequestBody AutoWithdrawalRuleSet ruleset,	LithiumTokenUtil tokenUtil) {
		log.debug("AdminAutoWithdrawalRuleSetController.create [ruleset=" + ruleset + "]");
		try {
			AutoWithdrawalRuleSetDto rulesetDto = convertService.ruleSetToDto(service.create(domainName, ruleset, tokenUtil.guid()));
			return Response.<AutoWithdrawalRuleSetDto>builder().data(rulesetDto).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to create ruleset [ruleset="+ruleset+"] " + e.getMessage(), e);
			return failedRuleSetResponse(e);
		}
	}

	@PostMapping("/{domainName}/update")
	public Response<AutoWithdrawalRuleSetDto> updateRuleSet(@RequestBody AutoWithdrawalRuleSet ruleset,
			LithiumTokenUtil tokenUtil) {
		log.debug("AdminAutoWithdrawalRuleSetController.update [ruleset=" + ruleset + "]");
		try {
			AutoWithdrawalRuleSetDto rulesetDto = convertService.ruleSetToDto(service.updateRuleSet(ruleset, tokenUtil.guid()));
			return Response.<AutoWithdrawalRuleSetDto>builder()
					.data(rulesetDto)
					.status(Response.Status.OK)
					.build();
		} catch (Exception e) {
			log.error("Failed to update ruleset [ruleset=" + ruleset + "] " + e.getMessage(), e);
			return failedRuleSetResponse(e);
		}
	}

	@PostMapping("/{domainName}/{id}/changename")
	public Response<AutoWithdrawalRuleSetDto> changeName(@PathVariable("domainName") String domainName,
	                                                  @PathVariable("id") AutoWithdrawalRuleSet ruleset,
													  @RequestParam("newName") String newName,
													  LithiumTokenUtil tokenUtil) {
		log.debug("AdminAutoWithdrawalRuleSetController.changeName [ruleset="+ruleset+", newName="+newName+"]");
		try {
			DomainValidationUtil.validate(domainName, ruleset.getDomain().getName());
			return Response.<AutoWithdrawalRuleSetDto>builder()
					.data(convertService.ruleSetToDto(service.changeName(ruleset, newName, tokenUtil.guid())))
					.status(Response.Status.OK)
					.build();
		} catch (Exception e) {
			log.error("Failed to change ruleset name [ruleset="+ruleset+", newName="+newName+"] " + e.getMessage(), e);
			return failedRuleSetResponse(e);
		}
	}

	@PostMapping("/{domainName}/{id}/change-delay")
	public Response<AutoWithdrawalRuleSetDto> changeDelay(@PathVariable("domainName") String domainName,
													  @PathVariable("id") AutoWithdrawalRuleSet ruleset,
													  @RequestParam(name="newDelay", required=false) Long newDelay,
													  @RequestParam("delayedStart") boolean delayedStart,
													  LithiumTokenUtil tokenUtil) {
		log.debug("AdminAutoWithdrawalRuleSetController.changeDelay [ruleset="+ruleset+", newDelay="+newDelay+"]");
		try {
			DomainValidationUtil.validate(domainName, ruleset.getDomain().getName());
			ruleset = service.changeDelay(ruleset, delayedStart, newDelay, tokenUtil.guid());
			return Response.<AutoWithdrawalRuleSetDto>builder()
					.data(convertService.ruleSetToDto(ruleset))
					.status(Response.Status.OK)
					.build();
		} catch (Exception e) {
			log.error("Failed to change ruleset delay [ruleset="+ruleset+", newDelay="+newDelay+", newDelayedStart="+delayedStart+"] " + e.getMessage(), e);
			return failedRuleSetResponse(e);
		}
	}

	@PostMapping("/{domainName}/{id}/delete")
	public Response<AutoWithdrawalRuleSetDto> delete(@PathVariable("domainName") String domainName,
	                                              @PathVariable("id") AutoWithdrawalRuleSet ruleset,
												  LithiumTokenUtil tokenUtil)
			throws Status500InternalServerErrorException {
		log.debug("AdminAutoWithdrawalRuleSetController.delete [ruleset="+ruleset+"]");
		try {
			DomainValidationUtil.validate(domainName, ruleset.getDomain().getName());
			ruleset = service.deleteRuleset(ruleset, tokenUtil.guid());
			return Response.<AutoWithdrawalRuleSetDto>builder()
					.data(convertService.ruleSetToDto(ruleset))
					.status(Response.Status.OK)
					.build();
		} catch (Exception e) {
			log.error("Failed to delete ruleset [ruleset="+ruleset+"] " + e.getMessage(), e);
			return failedRuleSetResponse(e);
		}
	}

	@PostMapping("/{domainName}/{id}/toggle/enabled")
	public Response<AutoWithdrawalRuleSetDto> toggleEnabled(
		@PathVariable("domainName") String domainName,
		@PathVariable("id") AutoWithdrawalRuleSet ruleset,
		LithiumTokenUtil tokenUtil
	) {
		log.debug("AdminAutoWithdrawalRuleSetController.toggleEnabled [ruleset="+ruleset+"]");
		try {
			DomainValidationUtil.validate(domainName, ruleset.getDomain().getName());
			ruleset = service.toggleEnabled(ruleset, tokenUtil.guid());
			return Response.<AutoWithdrawalRuleSetDto>builder()
					.data(convertService.ruleSetToDto(ruleset))
					.status(Response.Status.OK)
					.build();
		} catch (Exception e) {
			log.error("Failed to toggle enabled flag on ruleset [ruleset="+ruleset+"] " + e.getMessage(), e);
			return failedRuleSetResponse(e);
		}
	}

	@PostMapping("/{domainName}/{id}/rule/add")
	public Response<AutoWithdrawalRuleSetDto> addRule(
		@PathVariable("domainName") String domainName,
		@PathVariable("id") AutoWithdrawalRuleSet ruleset,
		@RequestBody AutoWithdrawalRule rule,
		LithiumTokenUtil tokenUtil
	) {
		log.debug("AdminAutoWithdrawalRuleSetController.addRule [ruleset="+ruleset+", rule="+rule+"]");
		try {
			DomainValidationUtil.validate(domainName, ruleset.getDomain().getName());
			return Response.<AutoWithdrawalRuleSetDto>builder()
					.data(convertService.ruleSetToDto(service.addRule(ruleset, rule, tokenUtil.guid())))
					.status(Response.Status.OK)
					.build();
		} catch (Exception e) {
			log.error("Failed to add rule to ruleset [ruleset="+ruleset+", rule="+rule+"] " + e.getMessage(), e);
			return failedRuleSetResponse(e);
		}
	}

	@PostMapping("/{domainName}/{id}/rule/{ruleId}/update")
	public Response<AutoWithdrawalRuleSetDto> updateRule(
		@PathVariable("domainName") String domainName,
		@PathVariable("id") AutoWithdrawalRuleSet ruleset,
		@PathVariable("ruleId") AutoWithdrawalRule rule,
		@RequestBody AutoWithdrawalRule ruleUpdate,
		LithiumTokenUtil tokenUtil
	) {
		log.debug("AdminAutoWithdrawalRuleSetController.updateRule [ruleset="+ruleset+", rule="+rule+"]");
		try {
			DomainValidationUtil.validate(domainName, ruleset.getDomain().getName());
			ruleset = service.updateRule(ruleset, rule, ruleUpdate, tokenUtil.guid());
			return Response.<AutoWithdrawalRuleSetDto>builder()
					.data(convertService.ruleSetToDto(ruleset))
					.status(Response.Status.OK)
					.build();
		} catch (Exception e) {
			log.error("Failed to update rule on ruleset [ruleset="+ruleset+", rule="+rule
				+", ruleUpdate="+ruleUpdate+"] " + e.getMessage(), e);
			return failedRuleSetResponse(e);
		}
	}

	@PostMapping("/{domainName}/{id}/rule/{ruleId}/delete")
	public Response<AutoWithdrawalRuleSetDto> deleteRule(
		@PathVariable("domainName") String domainName,
		@PathVariable("id") AutoWithdrawalRuleSet ruleset,
		@PathVariable("ruleId") AutoWithdrawalRule rule,
		LithiumTokenUtil tokenUtil
	) {
		log.debug("AdminAutoWithdrawalRuleSetController.deleteRule [ruleset="+ruleset+", rule="+rule+"]");
		try {
			DomainValidationUtil.validate(domainName, ruleset.getDomain().getName());
			ruleset = service.deleteRule(ruleset, rule, tokenUtil.guid());
			return Response.<AutoWithdrawalRuleSetDto>builder()
					.data(convertService.ruleSetToDto(ruleset))
					.status(Response.Status.OK)
					.build();
		} catch (Exception e) {
			log.error("Failed to delete rule on ruleset [ruleset="+ruleset+", rule="+rule+"] " + e.getMessage(), e);
			return failedRuleSetResponse(e);
		}
	}

	@PostMapping("/{domainName}/{id}/queueprocess")
	public Response<AutoWithdrawalRuleSetProcess> queueProcess(@PathVariable("domainName") String domainName,
	                                                           @PathVariable("id") AutoWithdrawalRuleSet ruleset,
															   LithiumTokenUtil tokenUtil) {
		log.debug("AdminAutoWithdrawalRuleSetController.queueprocess [ruleset="+ruleset+"]");
		try {
			DomainValidationUtil.validate(domainName, ruleset.getDomain().getName());
			AutoWithdrawalRuleSetProcess process = service.queueAutoWithdrawalRulesetProcess(ruleset, tokenUtil.guid());
			return Response.<AutoWithdrawalRuleSetProcess>builder().data(process).status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to queue ruleset process [ruleset="+ruleset+"] " + e.getMessage(), e);
			return Response.<AutoWithdrawalRuleSetProcess>builder()
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.message(e.getMessage())
					.build();
		}
	}

	@GetMapping("/{id}/changelogs")
	public @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable Long id, @RequestParam int p,
	                                                     LithiumTokenUtil tokenUtil) throws Exception {
		AutoWithdrawalRuleSet ruleset = service.findById(id);
		DomainValidationUtil.validate(ruleset.getDomain().getName(), "AUTOWITHDRAWALS_RULESETS_VIEW", tokenUtil);
		return service.getChangeLogs(id, new String[] {
			"auto-withdrawal-ruleset", "auto-withdrawal-ruleset.rule", "auto-withdrawal-ruleset.process"}, p);
	}

	private Response<AutoWithdrawalRuleSetDto> failedRuleSetResponse(Exception e) {
		return Response.<AutoWithdrawalRuleSetDto>builder()
				.status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage())
				.build();
	}
}
