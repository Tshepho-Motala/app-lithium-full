package lithium.service.cashier.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.objects.SummaryLabelValue;
import lithium.service.cashier.ProcessorType;
import lithium.service.cashier.data.entities.Domain;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.Image;
import lithium.service.cashier.data.objects.DomainMethodOrder;
import lithium.service.cashier.data.views.Views;
import lithium.service.cashier.services.CashierFrontendService;
import lithium.service.cashier.services.DomainMethodProcessorService;
import lithium.service.cashier.services.DomainMethodService;
import lithium.service.cashier.services.DomainService;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/cashier/dm")
public class DomainMethodController {
	@Autowired
	private DomainMethodService domainMethodService;
	@Autowired
	private DomainMethodProcessorService domainMethodProcessorService;
	@Autowired
	private CashierFrontendService cashierFrontendService;
	@Autowired
	private ChangeLogService changeLogService;
	@Autowired
	private DomainService domainService;

	//View Frontend
	@GetMapping("/frontend/processors")
	@JsonView(Views.ProcessedProcessor.class)
	public Response<?> processors(
			@RequestParam("methodId") Long methodId,
			@RequestParam("userGuid") String userGuid,
			@RequestParam("ipAddr") String ipAddr,
			@RequestParam("userAgent") String userAgent,
			HttpServletRequest request) {
		List<?> domainMethodProcessors = cashierFrontendService.domainMethodProcessors(methodId, userGuid, ipAddr, userAgent);
		return Response.<List<?>>builder().data(domainMethodProcessors).status(Status.OK).build();
	}

	@GetMapping("/frontend/deposit")
	@JsonView(Views.ProcessedProcessor.class)
	public Response<?> methodsDeposit(
			@RequestParam("userGuid") String userGuid,
			@RequestParam("ipAddr") String ipAddr,
			@RequestParam("userAgent") String userAgent,
			HttpServletRequest request
	) {
		log.info("Lookup Deposit Methods for userGuid:" + userGuid + " ipAddr:" + ipAddr + " userAgent:" + userAgent);

		if (ipAddr == null) ipAddr = request.getRemoteAddr();
		if (userAgent == null) userAgent = request.getHeader("User-Agent");

		List<?> domainMethods = cashierFrontendService.methodsDeposit(userGuid, ipAddr, userAgent);

		return Response.<List<?>>builder().data(domainMethods).status(Status.OK).build();
	}
	@GetMapping("/frontend/withdraw")
	@JsonView(Views.ProcessedProcessor.class)
	public Response<?> methodsWithdraw(
		@RequestParam("userGuid") String userGuid,
		@RequestParam("ipAddr") String ipAddr,
		@RequestParam("userAgent") String userAgent,
		HttpServletRequest request
	) {
		log.info("Lookup Withdraw Methods for userName:"+userGuid.split("/")[1]+" domainName:"+userGuid.split("/")[0]+" ipAddr:"+ipAddr+" userAgent:"+userAgent);

		if (ipAddr==null) ipAddr = request.getRemoteAddr();
		if (userAgent==null) userAgent = request.getHeader("User-Agent");

		List<?> domainMethods = cashierFrontendService.methodsWithdraw(userGuid, ipAddr, userAgent);

		return Response.<List<?>>builder().data(domainMethods).status(Status.OK).build();
	}

	// non frontend view

	@JsonView(Views.Public.class)
	@PostMapping("/method/{methodId}")
	public Response<?> linkDomainMethod(
		@PathVariable("methodId") Long methodId,
		@RequestParam("name") String name,
		@RequestParam("enabled") boolean enabled,
		@RequestParam("deposit") boolean deposit,
		@RequestParam("priority") int priority,
		@RequestParam("domainName") String domainName,
		LithiumTokenUtil token
	) throws Exception {
		log.info("Creating DomainMethod link.");
		return Response.<DomainMethod>builder()
			.data(
				domainMethodService.create(name, null, null, null, enabled, deposit, priority, methodId, domainName, token.guid(), token.userLegalName())
			)
			.status(Status.OK)
			.build();
	}

	@JsonView(Views.Public.class)
	@PostMapping("/method")
	public Response<?> linkDomainMethod(
		@RequestParam("name") String name,
		@RequestParam("enabled") boolean enabled,
		@RequestParam("priority") int priority,
		@RequestParam("methodName") String methodName,
		@RequestParam("methodUrl") String methodUrl,
		@RequestParam("domainName") String domainName,
		LithiumTokenUtil token
	) throws Exception {
		log.info("Creating DomainMethod link.");
		return Response.<DomainMethod>builder()
			.data(
				domainMethodService.create(name, null, null, null, enabled, priority, methodName, methodUrl, domainName, token.guid(), token.userLegalName())
			)
			.status(Status.OK)
			.build();
	}

	@PutMapping
	public Response<?> update(@RequestBody DomainMethod domainMethod, LithiumTokenUtil token) {

		return Response.<DomainMethod>builder()
			.data(domainMethodService.saveDomainMethod(domainMethod, token.guid(), token.userLegalName()))
			.status(Status.OK)
			.build();
	}

	@PutMapping("/multiple")
	public Response<?> updateMultiple(@RequestBody List<DomainMethod> domainMethods, LithiumTokenUtil token) {
		List<DomainMethod> dms = new ArrayList<>();
		if (!domainMethods.isEmpty()) {
			DomainMethod aDomainMethod = domainMethods.get(0);
			DomainMethodOrder domainMethodsOrderOld = domainMethodService.getDomainMethodsOrder(aDomainMethod);
			domainMethods.forEach(dm -> dms.add(domainMethodService.saveDomainMethod(dm, token.guid(),true, token.userLegalName())));
			DomainMethodOrder domainMethodsOrder = domainMethodService.getDomainMethodsOrder(aDomainMethod);
			domainMethodService.saveChangelogForDomainMethodOrder(aDomainMethod, domainMethodsOrderOld, domainMethodsOrder, token.guid(), token.userLegalName());
		}
		return Response.<List<DomainMethod>>builder()
			.data(dms)
			.status(Status.OK)
			.build();
	}

	@JsonView(Views.Public.class)
	@GetMapping("/domain/{domainName}/deposit")
	public Response<?> findDepositDomainMethods(
		@PathVariable("domainName") String domainName
	) {
		return Response.<List<DomainMethod>>builder()
			.data(domainMethodService.list(domainName, ProcessorType.DEPOSIT))
			.status(Status.OK)
			.build();
	}
	@JsonView(Views.Image.class)
	@GetMapping("/domain/{domainName}/deposit/image")
	public Response<?> findDepositDomainMethodsImage(
		@PathVariable("domainName") String domainName
	) {
		return findDepositDomainMethods(domainName);
	}

	@JsonView(Views.Public.class)
	@GetMapping("/domain/{domainName}/withdraw")
	public Response<?> findWithdrawDomainMethods(
		@PathVariable("domainName") String domainName
	) {
		return Response.<List<DomainMethod>>builder()
			.data(domainMethodService.list(domainName, ProcessorType.WITHDRAW))
			.status(Status.OK)
			.build();
	}
	@JsonView(Views.Image.class)
	@GetMapping("/domain/{domainName}/withdraw/image")
	public Response<?> findWithdrawDomainMethodsImage(
		@PathVariable("domainName") String domainName
	) {
		return findWithdrawDomainMethods(domainName);
	}

	@JsonView(Views.Image.class)
	@GetMapping("/domain/{domainName}/spec")
	public Response<?> findDomainMethodsSpec(
		@PathVariable("domainName") String domainName
	) {
		return Response.<List<DomainMethod>>builder()
			.data(
				domainMethodService.findAll(domainName)
			)
			.status(Status.OK)
			.build();
	}

	@JsonView(Views.Public.class)
	@GetMapping("/{domainMethodId}")
	public Response<?> find(
		@PathVariable("domainMethodId") DomainMethod domainMethod
	) {
		return Response.<DomainMethod>builder()
			.data(domainMethodService.fillInMissingImage(domainMethod))
			.status(Status.OK)
			.build();
	}
	@JsonView(Views.Image.class)
	@GetMapping("/{domainMethodId}/image")
	public Response<?> findImage(
		@PathVariable("domainMethodId") DomainMethod domainMethod
	) {
		return Response.<DomainMethod>builder()
			.data(domainMethodService.fillInMissingImage(domainMethod))
			.status(Status.OK)
			.build();
	}

	@JsonView(Views.Image.class)
	@GetMapping("/{domainMethodId}/imageonly")
	public Response<?> domainMethodImageOnly(
		@PathVariable("domainMethodId") DomainMethod domainMethod
	) throws IOException {
		return Response.<Image>builder()
			.data(domainMethodService.fillInMissingImage(domainMethod).getImage())
			.status(Status.OK)
			.build();
	}

	@JsonView(Views.Public.class)
	@GetMapping("/{domainMethodId}/processors")
	public Response<?> processors(
		@PathVariable("domainMethodId") DomainMethod domainMethod
	) {
		return Response.<List<DomainMethodProcessor>>builder()
			.data(domainMethodProcessorService.list(domainMethod.getId()))
			.status(Status.OK)
			.build();
	}

	@JsonView(Views.Public.class)
	@GetMapping("/{domainMethodId}/enable")
	public Response<?> toggleEnable(@PathVariable("domainMethodId") DomainMethod domainMethod, LithiumTokenUtil token) {

		return Response.<DomainMethod>builder()
			.data(domainMethodService.toggleEnable(domainMethod, token.guid(), token.userLegalName()))
			.status(Status.OK)
			.build();
	}

	@GetMapping("/{domainMethodId}/totals")
	@JsonView(Views.Public.class)
	public Response<?> findTotals(
		@PathVariable("domainMethodId") DomainMethod domainMethod
	) {
		try {
			return Response.<List<Map<String, SummaryLabelValue>>>builder()
				.data(Arrays.asList(domainMethodService.accountingTotals(domainMethod)))
				.status(Status.OK)
				.build();
		} catch (Exception ex) {
			log.error("Error getting domain method totals: " + domainMethod, ex);
		}

		return Response.<List<Map<String, SummaryLabelValue>>>builder()
				.status(Status.INTERNAL_SERVER_ERROR)
				.build();
	}

	@GetMapping("/{domainMethodId}/totals/{username}")
	@JsonView(Views.Public.class)
	public Response<?> findTotals(
		@PathVariable("domainMethodId") DomainMethod domainMethod,
		@PathVariable("username") String username
	) {
		try {
			return Response.<List<Map<String, SummaryLabelValue>>>builder()
					.data(Arrays.asList(domainMethodService.accountingTotals(domainMethod, username)))
					.status(Status.OK)
					.build();
		} catch (Exception ex) {
			log.error("Error getting domain method totals: " + domainMethod, ex);
		}

		return Response.<List<Map<String, SummaryLabelValue>>>builder()
				.status(Status.INTERNAL_SERVER_ERROR)
				.build();
	}

	@DeleteMapping("/{domainMethodId}")
	@JsonView(Views.Public.class)
	public Response<?> delete(@PathVariable("domainMethodId") DomainMethod domainMethod, LithiumTokenUtil token) {
		return Response.<DomainMethod>builder()
				.data(domainMethodService.delete(domainMethod, token.guid(), token.userLegalName()))
				.status(Status.OK)
				.build();
	}

    @GetMapping("/changelogs")
    private @ResponseBody Response<ChangeLogs> changeLogs(@RequestParam String domainName, @RequestParam boolean deposit, @RequestParam int p) throws Exception {
        log.debug("cashier dm order changelog request: " + domainName + ", deposit: " + deposit);
		Domain domain = domainService.findByName(domainName);
		return changeLogService.listLimited(ChangeLogRequest.builder()
                .entityRecordId(domain.getId())
                .entities(new String[] { "dm." + (deposit ? "deposit" : "withdraw") })
                .page(p)
                .build()
        );
    }
}
