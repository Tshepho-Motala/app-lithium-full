package lithium.service.accounting.provider.internal.controllers;

import lithium.exceptions.Status415NegativeBalanceException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.LithiumMetricsService;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.client.stream.auxlabel.AuxLabelStream;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.objects.AdjustMultiRequest;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.accounting.objects.AuxLabelStreamData;
import lithium.service.accounting.objects.PlayerBalanceResponse;
import lithium.service.accounting.objects.TransactionStreamData;
import lithium.service.accounting.provider.internal.config.Properties;
import lithium.service.accounting.provider.internal.data.entities.LabelValue;
import lithium.service.accounting.provider.internal.data.repositories.AccountLabelValueConstraintRepository;
import lithium.service.accounting.provider.internal.data.repositories.AccountRepository;
import lithium.service.accounting.provider.internal.data.repositories.AccountTypeRepository;
import lithium.service.accounting.provider.internal.data.repositories.CurrencyRepository;
import lithium.service.accounting.provider.internal.data.repositories.DomainRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionEntryRepository;
import lithium.service.accounting.provider.internal.data.repositories.TransactionLabelValueRepository;
import lithium.service.accounting.provider.internal.data.repositories.UserRepository;
import lithium.service.accounting.provider.internal.events.BalanceAdjustEvent;
import lithium.service.accounting.provider.internal.services.AdjustMultiBatchService;
import lithium.service.accounting.provider.internal.services.BalanceService;
import lithium.service.accounting.provider.internal.services.PeriodService;
import lithium.service.accounting.provider.internal.services.TransactionService;
import lithium.service.accounting.provider.internal.services.TransactionServiceWrapper;
import lithium.service.affiliate.client.stream.TransactionStream;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/balance")
public class BalanceController {
	@Autowired BalanceService balanceService;
	@Autowired TransactionService transactionService;
	@Autowired AccountTypeRepository accountTypeRepository;
	@Autowired AccountCodeController accountCodeService;
	@Autowired AccountRepository accountRepository;
	@Autowired DomainRepository domainRepository;
	@Autowired UserRepository userRepository;
	@Autowired CurrencyRepository currencyRepository;
	@Autowired PeriodService periodService;
	@Autowired AccountLabelValueConstraintRepository accountLabelValueConstraintRepository;
	@Autowired TransactionEntryRepository transactionEntryRepository;
	@Autowired TransactionLabelValueRepository transactionLabelValueRepository;
	@Autowired LithiumMetricsService metrics;
	@Autowired LithiumServiceClientFactory lithiumServiceClientFactory;
	@Autowired TransactionServiceWrapper transactionServiceWrapper;
	@Autowired TransactionStream transactionStream;
	@Autowired AuxLabelStream auxLabelStream;
	@Autowired ModelMapper mapper;
	@Autowired AdjustMultiBatchService adjustMultiBatchService;
	@Autowired CachingDomainClientService domainClientService;
	@Autowired Properties properties;

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
		return transactionServiceWrapper.getBalance(domainName, accountCode, accountType, currencyCode, ownerDomain+"/"+owner);
	}

	@RequestMapping("/getAllByOwnerGuid")
	public Response<List<PlayerBalanceResponse>> getAllByOwnerGuid(
			@RequestParam("domainName") String domainName,
			@RequestParam("ownerGuid") String ownerGuid
	) throws Exception {
		return Response.<List<PlayerBalanceResponse>>builder().data(balanceService.allBalances(domainName, ownerGuid)).status(Status.OK).build();
	}

	@RequestMapping("/getByOwnerGuid")
	public Response<Long> getByOwnerGuid(
			@RequestParam("domainName") String domainName,
			@RequestParam("accountCode") String accountCode,
			@RequestParam("accountType") String accountType,
			@RequestParam("currencyCode") String currencyCode,
			@RequestParam("ownerGuid") String ownerGuid) throws Exception {
		return transactionServiceWrapper.getBalance(domainName, accountCode, accountType, currencyCode, ownerGuid);
	}

	@RequestMapping("/getByAccountType")
	@Deprecated
	public Response<Map<String, Long>> getByAccountType(
			@RequestParam("domainName") String domainName,
			@RequestParam("accountType") String accountType,
			@RequestParam("currencyCode") String currencyCode,
			@RequestParam("ownerGuid") String ownerGuid) throws Exception {
		return transactionServiceWrapper.getBalanceByAccountType(domainName, accountType, currencyCode, ownerGuid);
	}



	@RequestMapping("/get")
	public Response<Long> get(
			@RequestParam String currencyCode,
			@RequestParam String domainName,
			@RequestParam String ownerGuid) throws Exception {
		return transactionServiceWrapper.getBalance(domainName, null, null, currencyCode, ownerGuid);
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
        return handleAdjustMultiTransaction(amountCents, date, accountCode, accountTypeCode, transactionTypeCode, contraAccountCode, contraAccountTypeCode, labels, currencyCode, domainName, ownerGuid, authorGuid, allowNegativeAdjust, null);
	}

    @Deprecated
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
	) throws Status414AccountingTransactionDataValidationException, Status415NegativeBalanceException, Status500InternalServerErrorException {
        return handleAdjustMultiTransaction(amountCents, date, accountCode, accountTypeCode, transactionTypeCode, contraAccountCode, contraAccountTypeCode, labels, currencyCode, domainName, ownerGuid, authorGuid, allowNegativeAdjust, negAdjProbeAccCodes);
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
        return handleAdjustMultiTransaction(amountCents, date, "PLAYER_BALANCE", "PLAYER_BALANCE",
				transactionTypeCode, contraAccountCode, contraAccountTypeCode,
				labels, currencyCode, domainName, ownerGuid, authorGuid, allowNegativeAdjust, null);
	}

    @RequestMapping(path= "/v2/adjustMulti",  method= RequestMethod.POST)
    public Response<AdjustmentTransaction> adjustMultiV2(
            @RequestBody AdjustMultiRequest request
    ) throws
			Status414AccountingTransactionDataValidationException, Status415NegativeBalanceException, Status500InternalServerErrorException
    {
        return handleAdjustMultiTransaction(request.getAmountCents(),
                request.getDate(),
                request.getAccountCode(),
                request.getAccountTypeCode(),
                request.getTransactionTypeCode(),
                request.getContraAccountCode() ,
                request.getContraAccountTypeCode(),
                request.getLabels(),
                request.getCurrencyCode(),
                request.getDomainName(),
                request.getOwnerGuid(),
                request.getAuthorGuid(),
                request.isAllowNegativeAdjust(),
                request.getNegAdjProbeAccCodes());
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

		TransactionStreamData transactionStreamData = TransactionStreamData.builder().build();
		List<LabelValue> summaryLabelValues = new ArrayList<LabelValue>();

		Response<AdjustmentTransaction> response = transactionServiceWrapper.rollback(date, reversalTransactionTypeCode, reversalLabelName,
				domainName, ownerGuid, authorGuid, currencyCode, labelName, labelValue, originalAccountCode, originalAccountTypeCode, transactionStreamData, summaryLabelValues);

		log.info("AdjustmentTransaction :: "+response);

		if (transactionStreamData.getTransactionId() != null) {
			log.debug("Dispatch affiliate stream data: " + transactionStreamData);
			transactionStream.register(transactionStreamData);
		}

		if (transactionStreamData.getTransactionId() != null) {
			log.debug("Dispatch affiliate stream data: " + transactionStreamData);
			transactionStream.register(transactionStreamData);

			HashSet<lithium.service.accounting.objects.LabelValue> lvList = new HashSet<>();
			summaryLabelValues.forEach(lv -> {
				lvList.add(mapper.map(lv, lithium.service.accounting.objects.LabelValue.class));
			});
			if (response.isSuccessful() && response.getData().isNew()) {
				AuxLabelStreamData entry = AuxLabelStreamData.builder()
						.transactionId(transactionStreamData.getTransactionId())
						.labelValueList(new ArrayList<>(lvList))
						.build();
				log.debug("Register AuxLabelStreamData(rollback ): " + entry);
				if (properties.getBalanceAdjustments().isSummarizeEnabled()) {
					auxLabelStream.register(entry);
				}
			}
			//transactionService.summarizeAdditionalTransactionLabels(transactionStreamData.getTransactionId(), summaryLabelValues);

			if (properties.getBalanceAdjustments().isDispatchUserBalanceEventEnabled()) {
				transactionServiceWrapper.dispatchUserBalanceEvent(ownerGuid, domainName, currencyCode);
			}
		}

		return response;
	}

    public Response<AdjustmentTransaction> handleAdjustMultiTransaction(Long amountCents, DateTime date, String accountCode, String accountTypeCode, String transactionTypeCode, String contraAccountCode, String contraAccountTypeCode, String[] labels, String currencyCode, String domainName, String ownerGuid, String authorGuid, Boolean allowNegativeAdjust, String[] negAdjProbeAccCodes)
			throws Status414AccountingTransactionDataValidationException, Status415NegativeBalanceException, Status500InternalServerErrorException {
        TransactionStreamData transactionStreamData = TransactionStreamData.builder().build();
        List<LabelValue> summaryLabelValues = new ArrayList<>();

        Response<AdjustmentTransaction> response = transactionServiceWrapper.adjustMulti(amountCents, date, accountCode, accountTypeCode,
                transactionTypeCode, contraAccountCode, contraAccountTypeCode, labels, currencyCode,
                domainName, ownerGuid, authorGuid, allowNegativeAdjust, negAdjProbeAccCodes, transactionStreamData, summaryLabelValues, null, new BalanceAdjustEvent(), false);

        if (transactionStreamData.getTransactionId() != null) {
            log.debug("Dispatch affiliate stream data: " + transactionStreamData);
            transactionStream.register(transactionStreamData);

            HashSet<lithium.service.accounting.objects.LabelValue> lvList = new HashSet<>();
            summaryLabelValues.forEach(lv -> {
                lvList.add(mapper.map(lv, lithium.service.accounting.objects.LabelValue.class));
            });
            if (response.isSuccessful() && response.getData().isNew()) {
                AuxLabelStreamData entry = AuxLabelStreamData.builder()
                        .transactionId(transactionStreamData.getTransactionId())
                        .labelValueList(new ArrayList<>(lvList))
                        .build();
                log.debug("Register AuxLabelStreamData(adjustMulti): " + entry);
                if (properties.getBalanceAdjustments().isSummarizeEnabled()) {
                    auxLabelStream.register(entry);
                }
            }
            //transactionService.summarizeAdditionalTransactionLabels(transactionStreamData.getTransactionId(), summaryLabelValues);

            if (properties.getBalanceAdjustments().isDispatchUserBalanceEventEnabled()) {
                transactionServiceWrapper.dispatchUserBalanceEvent(ownerGuid, domainName, currencyCode);
            }
        }
        return response;
    }


    //FIXME: Create a rollback that looks at the original transaction type and gets the unique constraint for the transaction type.
	// This is then used in conjunction with the account type to update the original transaction with the bound rollback label
	// And provide the rollback transaction with the original tran id as tieback and to avoid duplicates.
}
