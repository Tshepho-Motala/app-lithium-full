package lithium.service.cashier.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.objects.SummaryLabelValue;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorProperty;
import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.data.entities.Limits;
import lithium.service.cashier.data.entities.Processor;
import lithium.service.cashier.data.views.Views;
import lithium.service.cashier.services.DomainMethodProcessorService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/cashier/dmp")
public class DomainMethodProcessorController {
	
	@Autowired
	private ChangeLogService changeLogService;
	@Autowired
	private DomainMethodProcessorService domainMethodProcessorService;
	
	/*
	 * Create new DomainMethodProcessor (will update enabled and weight if existing record found.
	 */
	@PostMapping
	@JsonView(Views.Public.class)
	public Response<?> create(
		@RequestParam("enabled") Boolean enabled,
		@RequestParam("weight") Double weight,
		@RequestParam("domainMethodId") DomainMethod domainMethod,
		@RequestParam("description") String description,
		@RequestParam("processorId") Processor processor,
		@RequestParam(name="reserveFundsOnWithdrawal", required=false) Boolean reserveFundsOnWithdrawal,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		log.info("Creating DomainMethodProcessor link.");
		return Response.<DomainMethodProcessor>builder()
			.data(
				domainMethodProcessorService.create(domainMethod, processor, description, enabled, weight, reserveFundsOnWithdrawal, tokenUtil.guid(), tokenUtil.userLegalName())
			)
			.status(Status.OK)
			.build();
	}
	
	@PostMapping("/create")
	@JsonView(Views.Public.class)
	public Response<?> create(
		@RequestBody DomainMethodProcessor domainMethodProcessor,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		log.info("Creating DomainMethodProcessor link with limits and fees.");
		log.info("DomainMethodProcessor : "+domainMethodProcessor);
		return Response.<DomainMethodProcessor>builder()
			.data(
				domainMethodProcessorService.create(domainMethodProcessor, tokenUtil.guid(), tokenUtil.userLegalName())
			)
			.status(Status.OK)
			.build();
	}
	
	/*
	 * Update specific DomainMethodProcessor
	 */
	@PutMapping("/{id}")
	@JsonView(Views.Public.class)
	public Response<?> update(
		@PathVariable("id") Long id,
		@RequestParam("enabled") Boolean enabled,
		@RequestParam("weight") Double weight,
		@RequestParam("description") String description,
		@RequestParam(name="reserveFundsOnWithdrawal", required=false) Boolean reserveFundsOnWithdrawal,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		log.info("Updating DomainMethodProcessor "+id+" (enabled:"+enabled+"|weight:"+weight+")");
		return Response.<DomainMethodProcessor>builder()
			.data(domainMethodProcessorService.update(id, enabled, weight, description, reserveFundsOnWithdrawal, tokenUtil.guid(), tokenUtil.userLegalName()))
			.status(Status.OK)
			.build();
	}
	
	@PutMapping("/{id}/update")
	@JsonView(Views.Public.class)
	public Response<?> update(
		@RequestBody DomainMethodProcessor domainMethodProcessor,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		log.info("Updating DomainMethodProcessor : "+domainMethodProcessor);
		return Response.<DomainMethodProcessor>builder()
			.data(domainMethodProcessorService.save(domainMethodProcessor, tokenUtil.guid(), tokenUtil.userLegalName()))
			.status(Status.OK)
			.build();
	}
	
	@PutMapping("/multiple")
	public Response<?> updateMultiple(
		@RequestBody List<DomainMethodProcessor> domainMethodProcessors,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		List<DomainMethodProcessor> dmps = new ArrayList<>();
		for (DomainMethodProcessor dmp: domainMethodProcessors) {
			dmps.add(domainMethodProcessorService.save(dmp, tokenUtil.guid(), tokenUtil.userLegalName()));
		}
		return Response.<List<DomainMethodProcessor>>builder()
			.data(dmps)
			.status(Status.OK)
			.build();
	}
	
	@PutMapping("/{domainMethodProcessorId}/fees")
	@JsonView(Views.Public.class)
	public Response<?> updateFees(
		@PathVariable("domainMethodProcessorId") DomainMethodProcessor domainMethodProcessor,
		@RequestBody Fees fees,
		LithiumTokenUtil tokenUtil
	) throws Exception {
//		log.info("Saving Fees: "+fees+" for DMP : "+domainMethodProcessor);
		return Response.<DomainMethodProcessor>builder()
			.data(domainMethodProcessorService.saveFees(domainMethodProcessor, fees, tokenUtil.guid(), tokenUtil.userLegalName()))
			.status(Status.OK)
			.build();
	}
	@PutMapping("/{domainMethodProcessorId}/limits")
	@JsonView(Views.Public.class)
	public Response<?> updateLimits(
		@PathVariable("domainMethodProcessorId") DomainMethodProcessor domainMethodProcessor,
		@RequestBody Limits limits,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		log.info("Saving Limits: "+limits+" for DMP : "+domainMethodProcessor);
		return Response.<DomainMethodProcessor>builder()
			.data(domainMethodProcessorService.saveLimits(domainMethodProcessor, limits, tokenUtil.guid(), tokenUtil.userLegalName()))
			.status(Status.OK)
			.build();
	}
	@PutMapping("/{domainMethodProcessorId}/domainlimits")
	@JsonView(Views.Public.class)
	public Response<?> updateDomainLimits(
		@PathVariable("domainMethodProcessorId") DomainMethodProcessor domainMethodProcessor,
		@RequestBody Limits limits,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		log.info("Saving DomainLimits: "+limits+" for DMP : "+domainMethodProcessor);
		return Response.<DomainMethodProcessor>builder()
			.data(domainMethodProcessorService.saveDomainLimits(domainMethodProcessor, limits, tokenUtil.guid(), tokenUtil.userLegalName()))
			.status(Status.OK)
			.build();
	}
	
	@DeleteMapping("/{domainMethodProcessorId}")
	@JsonView(Views.Public.class)
	public Response<?> delete(
		@PathVariable("domainMethodProcessorId") DomainMethodProcessor domainMethodProcessor,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		return Response.<DomainMethodProcessor>builder()
			.data(domainMethodProcessorService.delete(domainMethodProcessor, tokenUtil.guid() , tokenUtil.userLegalName()))
			.status(Status.OK)
			.build();
	}
	
	@DeleteMapping("/{domainMethodProcessorId}/fees")
	@JsonView(Views.Public.class)
	public Response<?> deleteFees(
		@PathVariable("domainMethodProcessorId") DomainMethodProcessor domainMethodProcessor,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		return Response.<DomainMethodProcessor>builder()
			.data(domainMethodProcessorService.deleteFees(domainMethodProcessor, tokenUtil.guid(), tokenUtil.userLegalName()))
			.status(Status.OK)
			.build();
	}
	
	@DeleteMapping("/{domainMethodProcessorId}/limits")
	@JsonView(Views.Public.class)
	public Response<?> deleteLimits(
		@PathVariable("domainMethodProcessorId") DomainMethodProcessor domainMethodProcessor,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		return Response.<DomainMethodProcessor>builder()
			.data(domainMethodProcessorService.deleteLimits(domainMethodProcessor, tokenUtil.guid() , tokenUtil.userLegalName()))
			.status(Status.OK)
			.build();
	}
	
//	@GetMapping("/list/{domainMethodId}")
//	public Response<?> domainMethodProcessors(@PathVariable("domainMethodId") Long domainMethodId) {
//		return Response.<List<DomainMethodProcessor>>builder()
//			.data(domainMethodProcessorService.findDomainMethodProcessors(domainMethodId))
//			.status(Status.OK)
//			.build();
//	}
	
	/*
	 * View specific DomainMethodProcessor
	 */
	@GetMapping("/{id}")
	@JsonView(Views.Public.class)
	public Response<?> find(@PathVariable("id") Long id) {
		return Response.<DomainMethodProcessor>builder()
			.data(domainMethodProcessorService.find(id))
			.status(Status.OK)
			.build();
	}
	
	@GetMapping("/{id}/totals")
	@JsonView(Views.Public.class)
	public Response<?> findTotals(@PathVariable("id") DomainMethodProcessor domainMethodProcessor) {
		try {
			return Response.<List<Map<String, SummaryLabelValue>>>builder()
				.data(Arrays.asList(domainMethodProcessorService.accountingTotals(domainMethodProcessor)))
				.status(Status.OK)
				.build();
		} catch (Exception ex) {
			log.error("problem getting domain method processor totals: " + domainMethodProcessor, ex);
		}
		
		return Response.<List<Map<String, SummaryLabelValue>>>builder()
				.status(Status.INTERNAL_SERVER_ERROR)
				.build();
	}
	@GetMapping("/{id}/totals/{username}")
	@JsonView(Views.Public.class)
	public Response<?> findTotals(
		@PathVariable("id") DomainMethodProcessor domainMethodProcessor,
		@PathVariable("username") String username
	) {
		try {
			return Response.<List<Map<String, SummaryLabelValue>>>builder()
				.data(Arrays.asList(domainMethodProcessorService.accountingTotals(domainMethodProcessor, username)))
				.status(Status.OK)
				.build();
		} catch (Exception ex) {
			log.error("problem getting domain method processor totals: " + domainMethodProcessor, ex);
		}
		
		return Response.<List<Map<String, SummaryLabelValue>>>builder()
				.status(Status.INTERNAL_SERVER_ERROR)
				.build();
	}
	
	@GetMapping("/{id}/image")
	@JsonView(Views.Image.class)
	public Response<?> findWithImage(@PathVariable("id") Long id) {
		return find(id);
	}
	
	@PutMapping("/{id}/props")
	@JsonView(Views.Public.class)
	public Response<?> updateProperties(
		@PathVariable("id") DomainMethodProcessor domainMethodProcessor,
		@RequestBody List<DomainMethodProcessorProperty> domainMethodProcessorProperties
	) throws Exception {
		log.debug("Updating DomainMethodProcessorProperties : "+domainMethodProcessorProperties);
		
		return Response.<List<DomainMethodProcessorProperty>>builder()
			.data(domainMethodProcessorService.saveProperties(domainMethodProcessor, domainMethodProcessorProperties))
			.status(Status.OK)
			.build();
	}
	/*
	 * View Properties for a specific DomainMethodProcessor
	 */
	@GetMapping("/{id}/props")
	@JsonView(Views.Public.class)
	public Response<?> propertiesWithDefaults(@PathVariable("id") Long domainMethodProcessorId) {
		return Response.<List<DomainMethodProcessorProperty>>builder()
			.data(domainMethodProcessorService.propertiesWithDefaults(domainMethodProcessorId))
			.status(Status.OK)
			.build();
	}
	@GetMapping("/{id}/props/nodef")
	@JsonView(Views.Public.class)
	public Response<?> properties(@PathVariable("id") Long domainMethodProcessorId) {
		return Response.<List<DomainMethodProcessorProperty>>builder()
			.data(domainMethodProcessorService.propertiesNoDefaults(domainMethodProcessorId))
			.status(Status.OK)
			.build();
	}
	
	@DeleteMapping("/{domainMethodProcessorId}/prop/{domainMethodProcessorPropertyId}")
	@JsonView(Views.Public.class)
	public Response<?> deleteProperty(
		@PathVariable("domainMethodProcessorId") Long domainMethodProcessorId,
		@PathVariable("domainMethodProcessorPropertyId") DomainMethodProcessorProperty domainMethodProcessorProperty
	) {
		return Response.<DomainMethodProcessorProperty>builder()
			.data(domainMethodProcessorService.removeProperty(domainMethodProcessorProperty))
			.status(Status.OK)
			.build();
	}
	
	@GetMapping("/{domainMethodProcessorId}/prop/{domainMethodProcessorPropertyId}")
	@JsonView(Views.Public.class)
	public Response<?> findProperty(
		@PathVariable("domainMethodProcessorId") Long domainMethodProcessorId,
		@PathVariable("domainMethodProcessorPropertyId") Long domainMethodProcessorPropertyId
	) {
		return Response.<DomainMethodProcessorProperty>builder()
			.data(domainMethodProcessorService.findProperty(domainMethodProcessorPropertyId))
			.status(Status.OK)
			.build();
	}
	
	
//	@PostMapping("/prop")
//	@JsonView(Views.Public.class)
//	public Response<?> saveProperty(
//		@RequestParam("processorPropertyId") Long processorPropertyId,
//		@RequestParam("domainMethodProcessorId") Long domainMethodProcessorId,
//		@RequestParam("value") String value
//	) throws Exception {
//		return Response.<DomainMethodProcessorProperty>builder()
//			.data(domainMethodProcessorService.saveProperty(processorPropertyId, domainMethodProcessorId, value))
//			.status(Status.OK)
//			.build();
//	}
	
	@GetMapping("/{domainMethodProcessorId}/changelogs")
	private @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable Long domainMethodProcessorId, @RequestParam int p) throws Exception {
		log.debug("cashier dmp changelog request " + domainMethodProcessorId);
		return changeLogService.listLimited(ChangeLogRequest.builder()
			.entityRecordId(domainMethodProcessorId)
			.entities(new String[] { "dmp", "dmp.fees", "dmp.limits" })
			.page(p)
			.build()
		);
	}
}