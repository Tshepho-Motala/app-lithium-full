package lithium.service.accounting.controllers;

import com.netflix.client.ClientException;
import lithium.exceptions.Status415NegativeBalanceException;
import lithium.metrics.LithiumMetricsService;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.objects.AdjustMultiRequest;
import lithium.service.accounting.objects.AdjustmentRequestComponent;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.accounting.objects.PlayerBalanceResponse;
import lithium.service.accounting.service.AccountingService;
import lithium.service.domain.client.objects.Provider;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/balance")
public class BalanceController {
	@Autowired LithiumMetricsService metrics;
	@Autowired ModelMapper mapper;
	@Autowired AccountingService accountingService;
	
	private String providerUrl(String domainName) {
		try {
			return metrics.timer(log).time("providerUrl", (StopWatch sw) -> {
				sw.start("Provider - "+domainName);
				Provider provider = accountingService.provider(domainName);
				log.debug("Provider :"+provider);
				sw.stop();
				return provider.getUrl();
			});
		} catch (Exception e) {
			log.error("Could not get provider url for domain :"+domainName);
		}
		return "";
	}
	
	//Added because Riaan doesn't know how to apply the permissions to the ant matchers in lithium.service.accounting.provider.internal.ServiceAccountingProviderInternalModuleInfo
	// Dont have time / feel like figuring it out now.
	@RequestMapping("/get/{domainName}/{accountCode}/{accountType}/{currencyCode}/{ownerDomain}/{owner:.+}")
	public Response<Long> getPath(
		@PathVariable("domainName") String domainName,
		@PathVariable("accountCode") String accountCode,
		@PathVariable("accountType") String accountType,
		@PathVariable("currencyCode") String currencyCode,
		@PathVariable("ownerDomain") String ownerDomain,
		@PathVariable("owner") String owner
	) throws Exception {
		return metrics.timer(log).time("getPath", (StopWatch sw) -> {
			try {
				sw.start("Provider");
				String providerUrl = providerUrl(domainName);
				sw.stop();
				sw.start("AccountingService - "+providerUrl);
				Response<Long> balanceResponse = accountingService.accountingClient(providerUrl).getPath(domainName, accountCode, accountType, currencyCode, ownerDomain, owner);
				sw.stop();
				log.debug("Return :"+balanceResponse);
				return balanceResponse;
			} catch (Exception e) {
				log.error("", e);
			}
			log.error("Returning zero.");
			return Response.<Long>builder().data(0L).build();
		});
	}

	@RequestMapping("/getAllByOwnerGuid")
	public Response<List<PlayerBalanceResponse>> getAllByOwnerGuid(
			@RequestParam("domainName") String domainName,
			@RequestParam("ownerGuid") String ownerGuid
	) throws Exception {
		return metrics.timer(log).time("getAllByOwnerGuid", (StopWatch sw) -> {
			log.debug("getAllByOwnerGuid("+domainName+", "+ownerGuid+")");
			String providerUrl = "";
			try {
				sw.start("Provider");
				providerUrl = providerUrl(domainName);
				sw.stop();
				sw.start("AccountingService - "+providerUrl);
				Response<List<PlayerBalanceResponse>> balances = accountingService.accountingClient(providerUrl).getAllByOwnerGuid(domainName, ownerGuid);
				log.debug("Return :"+balances);
				sw.stop();
				return balances;
			} catch (ConnectException | ClientException | RuntimeException e) {
				log.warn("Could not connect to "+providerUrl+" ("+domainName+"/all/balances/"+ownerGuid+")"+e.getMessage());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			log.error("Returning null.");
			return Response.<List<PlayerBalanceResponse>>builder().data(null).build();
		});
	}

	@RequestMapping("/{domainName}/getAllByOwnerGuid")
	public Response<List<PlayerBalanceResponse>> getAllByOwnerGuidV2(
		@PathVariable("domainName") String domainName,
		@RequestParam("ownerGuid") String ownerGuid
	) throws Exception {
		return getAllByOwnerGuid(domainName, ownerGuid);
	}
	
	@RequestMapping("/getByOwnerGuid")
	public Response<Long> getByOwnerGuid(
		@RequestParam("domainName") String domainName,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("accountType") String accountType,
		@RequestParam("currencyCode") String currencyCode,
		@RequestParam("ownerGuid") String ownerGuid
	) throws Exception {
		return metrics.timer(log).time("getByOwnerGuid", (StopWatch sw) -> {
			log.debug("getByOwnerGuid("+domainName+", "+accountCode+", "+accountType+", "+currencyCode+", "+ownerGuid+")");
			String providerUrl = "";
			try {
				sw.start("Provider");
				providerUrl = providerUrl(domainName);
				sw.stop();
				sw.start("AccountingService - "+providerUrl);
				Response<Long> balanceResponse = accountingService.accountingClient(providerUrl).getByOwnerGuid(domainName, accountCode, accountType, currencyCode, ownerGuid);
				log.debug("Return :"+balanceResponse);
				sw.stop();
				return balanceResponse;
			} catch (ConnectException | ClientException | RuntimeException e) {
				log.warn("Could not connect to "+providerUrl+" ("+domainName+"/"+accountCode+"/"+accountType+")"+e.getMessage());
				throw e;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw e;
			}
		});
	}
	
	@RequestMapping("/getByAccountType")
	public Response<Map<String, Long>> getByAccountType(
		@RequestParam("domainName") String domainName,
		@RequestParam("accountType") String accountType,
		@RequestParam("currencyCode") String currencyCode,
		@RequestParam("ownerGuid") String ownerGuid
	) throws Exception {
		return metrics.timer(log).time("getByAccountType", (StopWatch sw) -> {
			log.debug("getByAccountType("+domainName+", "+accountType+", "+currencyCode+", "+ownerGuid+")");
			String providerUrl = "";
			try {
				sw.start("Provider");
				providerUrl = providerUrl(domainName);
				sw.stop();
				sw.start("AccountingService - "+providerUrl);
				Response<Map<String, Long>> balanceResponse = accountingService.accountingClient(providerUrl).getByAccountType(domainName, accountType, currencyCode, ownerGuid);
				log.debug("Return :"+balanceResponse +" for (" +domainName+", "+accountType+", "+currencyCode+", "+ownerGuid+")");
				sw.stop();
				return balanceResponse;
			} catch (ConnectException | ClientException | RuntimeException e) {
				log.warn("Could not connect to "+providerUrl+" ("+domainName+"/"+accountType+")"+e.getMessage());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			log.error("Returning error for getByAccountType balance retrieval for: (" +domainName+", "+accountType+", "+currencyCode+", "+ownerGuid+")");
			return Response.<Map<String, Long>>builder().status(Status.SERVICE_UNAVAILABLE).build();
		});
	}
	
	@RequestMapping("/get")
	public Response<Long> get(
		@RequestParam String currencyCode, 
		@RequestParam String domainName, 
		@RequestParam String ownerGuid
	) throws Exception {
		return metrics.timer(log).time("get", (StopWatch sw) -> {
			try {
				sw.start("Provider");
				String providerUrl = providerUrl(domainName);
				sw.stop();
				sw.start("AccountingService - "+providerUrl);
				Response<Long> balanceResponse = accountingService.accountingClient(providerUrl).get(currencyCode, domainName, ownerGuid);
				log.debug("Return :"+balanceResponse);
				sw.stop();
				return balanceResponse;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			log.error("Returning zero.");
			return Response.<Long>builder().data(0L).build();
		});
	}

	@RequestMapping("/{domainName}/adjust/p")
	public Response<AdjustmentTransaction> adjustPath(
		@RequestParam("amountCents") Long amountCents,
		@RequestParam("date") @DateTimeFormat(iso=ISO.DATE_TIME) DateTime date,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("accountTypeCode") String accountTypeCode,
		@RequestParam("transactionTypeCode") String transactionTypeCode,
		@RequestParam("contraAccountCode") String contraAccountCode,
		@RequestParam("contraAccountTypeCode") String contraAccountTypeCode,
		@RequestParam(name="labels", required=false) String[] labels,
		@RequestParam("currencyCode") String currencyCode, 
		@PathVariable("domainName") String domainName, 
		@RequestParam("ownerGuid") String ownerGuid,
		@RequestParam("authorGuid") String authorGuid,
		@RequestParam(name="allowNegativeAdjust", required=false, defaultValue="true") Boolean allowNegativeAdjust) throws Exception {
		return adjustMulti(amountCents, date, accountCode, accountTypeCode, transactionTypeCode, contraAccountCode, contraAccountTypeCode, labels, currencyCode, domainName, ownerGuid, authorGuid, allowNegativeAdjust, null);
	}

	@RequestMapping("/adjustMulti")
	public Response<AdjustmentTransaction> adjustMulti(
		@RequestParam("amountCents") Long amountCents,
		@RequestParam("date") @DateTimeFormat(iso=ISO.DATE_TIME) DateTime date,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("accountTypeCode") String accountTypeCode,
		@RequestParam("transactionTypeCode") String transactionTypeCode,
		@RequestParam("contraAccountCode") String contraAccountCode,
		@RequestParam("contraAccountTypeCode") String contraAccountTypeCode,
		@RequestParam(name="labels", required=false) String[] labels,
		@RequestParam("currencyCode") String currencyCode, 
		@RequestParam("domainName") String domainName, 
		@RequestParam("ownerGuid") String ownerGuid,
		@RequestParam("authorGuid") String authorGuid,
		@RequestParam(name="allowNegativeAdjust", required=false, defaultValue="true") Boolean allowNegativeAdjust,
		@RequestParam(name="negAdjProbeAccCodes", required=false) String[] negAdjProbeAccCodes
	) throws Exception {
		return metrics.timer(log).time("adjustMulti", (StopWatch sw) -> {
			try {
				sw.start("Provider");
				String providerUrl = providerUrl(domainName);
				sw.stop();
				sw.start("AccountingService - "+providerUrl);
				Response<AdjustmentTransaction> adjustmentTransaction = accountingService.accountingClient(providerUrl).adjustMulti(amountCents, date.toString(), accountCode, accountTypeCode, transactionTypeCode, contraAccountCode, contraAccountTypeCode, labels, currencyCode, domainName, ownerGuid, authorGuid, allowNegativeAdjust, negAdjProbeAccCodes);
				log.debug("Return :"+adjustmentTransaction);
				sw.stop();
				return adjustmentTransaction;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw e;
			}
			//return Response.<AdjustmentTransaction>builder().data(null).status(Status.INTERNAL_SERVER_ERROR).build();
		});
	}

    @RequestMapping(path="/v2/adjustMulti",  method= RequestMethod.POST)
    public Response<AdjustmentTransaction> adjustMulti(
            @RequestBody AdjustMultiRequest request
    ) throws Exception {
        return metrics.timer(log).time("v2/adjustMulti", (StopWatch sw) -> {
            try {
                sw.start("Provider");
                String providerUrl = providerUrl(request.getDomainName());
                sw.stop();
                sw.start("AccountingService - "+providerUrl);
                Response<AdjustmentTransaction> adjustmentTransaction = accountingService.accountingClient(providerUrl).adjustMultiV2(request);
                log.debug("Return :"+adjustmentTransaction);
                sw.stop();
                return adjustmentTransaction;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw e;
            }
        });
    }


    @RequestMapping("/adjust")
	public Response<AdjustmentTransaction> adjust(
		@RequestParam("amountCents") Long amountCents,
		@RequestParam("date") @DateTimeFormat(iso=ISO.DATE_TIME) DateTime date,
		@RequestParam("transactionTypeCode") String transactionTypeCode,
		@RequestParam("contraAccountCode") String contraAccountCode,
		@RequestParam("contraAccountTypeCode") String contraAccountTypeCode,
		@RequestParam(name="labels", required=false) String[] labels,
		@RequestParam("currencyCode") String currencyCode, 
		@RequestParam("domainName") String domainName, 
		@RequestParam("ownerGuid") String ownerGuid,
		@RequestParam("authorGuid") String authorGuid,
		@RequestParam(name="allowNegativeAdjust", required=false, defaultValue="true") Boolean allowNegativeAdjust
	) throws Exception {
		return adjustMulti(
			amountCents,
			date,
			"PLAYER_BALANCE",
			"PLAYER_BALANCE",
			transactionTypeCode,
			contraAccountCode,
			contraAccountTypeCode,
			labels,
			currencyCode,
			domainName,
			ownerGuid,
			authorGuid,
			allowNegativeAdjust,
			null
		);
	}
	
	@RequestMapping("/rollback")
	public Response<AdjustmentTransaction> rollback(
		@RequestParam("date") @DateTimeFormat(iso=ISO.DATE_TIME) DateTime date,
		@RequestParam("reversalTransactionTypeCode") String reversalTransactionTypeCode,
		@RequestParam("reversalLabelName") String reversalLabelName,
		@RequestParam("domainName") String domainName, 
		@RequestParam("ownerGuid") String ownerGuid,
		@RequestParam("authorGuid") String authorGuid,
		@RequestParam("currencyCode") String currencyCode,
		@RequestParam("labelName") String labelName,
		@RequestParam("labelValue") String labelValue,
		@RequestParam("originalAccountCode") String originalAccountCode,
		@RequestParam("originalAccountTypeCode") String originalAccountTypeCode
	) throws Exception {
		return metrics.timer(log).time("rollback", (StopWatch sw) -> {
			try {
				sw.start("Provider");
				String providerUrl = providerUrl(domainName);
				sw.stop();
				sw.start("AccountingService - "+providerUrl);
				Response<AdjustmentTransaction> adjustmentTransaction = accountingService.accountingClient(providerUrl).rollback(date.toString(), reversalTransactionTypeCode, reversalLabelName, domainName, ownerGuid, authorGuid, currencyCode, labelName, labelValue, originalAccountCode, originalAccountTypeCode);
				log.debug("Return :"+adjustmentTransaction);
				sw.stop();
				return adjustmentTransaction;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw e;
			}
//			log.error("Returning zero.");
//			return Response.<AdjustmentTransaction>builder().build();
		});
	}

	@RequestMapping("/adjustMultiBatch")
	public Response<ArrayList<AdjustmentTransaction>> adjustMultiBatch(@RequestBody ArrayList<AdjustmentRequestComponent> adjustmentRequestList) throws Exception {
		return metrics.timer(log).time("adjustMultiBatch", (StopWatch sw) -> {
			try {
				sw.start("Provider");
				String providerUrl = providerUrl(adjustmentRequestList.get(0).getDomainName());
				sw.stop();
				sw.start("AccountingService - "+providerUrl);
				Response<ArrayList<AdjustmentTransaction>> adjustmentTransaction = accountingService.accountingClient(providerUrl).adjustMultiBatch(adjustmentRequestList);
				log.debug("Return :"+adjustmentTransaction);
				sw.stop();
				return adjustmentTransaction;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw e;
			}
//			log.error("Returning zero.");
//			return Response.<AdjustmentTransaction>builder().build();
		});
	}
}
