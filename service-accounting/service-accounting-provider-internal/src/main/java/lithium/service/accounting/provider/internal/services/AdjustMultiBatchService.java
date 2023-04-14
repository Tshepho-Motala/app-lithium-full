package lithium.service.accounting.provider.internal.services;

import lithium.exceptions.Status415NegativeBalanceException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.accounting.client.stream.auxlabel.AuxLabelStream;
import lithium.service.accounting.enums.Granularity;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.objects.AdjustmentRequestComponent;
import lithium.service.accounting.objects.AuxLabelStreamData;
import lithium.service.accounting.objects.TransactionStreamData;
import lithium.service.accounting.provider.internal.config.Properties;
import lithium.service.accounting.provider.internal.context.adjust.AdjustmentContext;
import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccount;
import lithium.service.accounting.provider.internal.data.entities.SummaryAccountTransactionType;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.affiliate.client.stream.TransactionStream;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AdjustMultiBatchService {

  @Autowired TransactionServiceWrapper transactionServiceWrapper;
  @Autowired TransactionStream transactionStream;
  @Autowired AuxLabelStream auxLabelStream;
  @Autowired ModelMapper mapper;
  @Autowired CurrencyService currencyService;
  @Autowired AccountService accountService;
  @Autowired PeriodService periodService;
  @Autowired DomainService domainService;
  @Autowired SummaryAccountService summaryAccountService;
  @Autowired SummaryAccountTransactionTypeService summaryAccountTransactionTypeService;
  @Autowired SummaryAccountLabelValueService summaryAccountLabelValueService;
  @Autowired TransactionTypeService transactionTypeService;
  @Autowired Properties properties;

  @Autowired LabelValueService labelValueService;

  public void adjustMultiBatch(AdjustmentContext context)
      throws Status414AccountingTransactionDataValidationException,
          Status415NegativeBalanceException, Status500InternalServerErrorException {

    // We need to create all database components that should not be rolled back as part of the
    // transaction
    // before the transaction starts. Populate inspects the request and findOrCreates all shared
    // objects.
    log.debug("populate {}", context);
    populate(context);

    // Before we start the transaction - we need to precreate the label values outside of the transaction,
    // but nothing that ties it to an accounting transaction yet.
    log.debug("precreateLabelValues {}", context);
    ArrayList<AdjustmentRequestComponent> adjustmentRequestList = context.getRequest().getAdjustments();

    for (AdjustmentRequestComponent adjReq : adjustmentRequestList) {

      String[] adjReqLabels = adjReq.getLabels() != null ? adjReq.getLabels() : null;
      if (adjReqLabels == null) continue;

      transactionServiceWrapper.precreateLabelValues(adjReqLabels);
    }

    // Here we start a transaction. Anything that goes wrong will roll back everything that happens
    // in here.
    log.debug("adjustMultiBatchInternal {}", context);
    transactionServiceWrapper.adjustMultiBatchInternal(context);

    // Send events to message queue of completed transaction. We want this outside of the
    // transaction in case the transaction
    // retries.
    log.debug("publishEvents {}", context);
    publishEvents(context);
  }

  private void populate(AdjustmentContext context)
      throws Status414AccountingTransactionDataValidationException {
    for (AdjustmentRequestComponent a : context.getRequest().getAdjustments()) {

      Domain domain = domainService.findOrCreate(a.getDomainName());
      Currency currency = currencyService.findOrCreate(a.getCurrencyCode());

      Account account =
          accountService.findOrCreate(
              a.getAccountCode(),
              a.getAccountTypeCode(),
              a.getCurrencyCode(),
              a.getDomainName(),
              a.getOwnerGuid());
      context.getAccountIdList().add(account.getId());
      context.getAccounts().add(account);

      Account contraAccount =
          accountService.findOrCreate(
              a.getContraAccountCode(),
              a.getContraAccountTypeCode(),
              a.getCurrencyCode(),
              a.getDomainName(),
              a.getOwnerGuid());
      context.getAccounts().add(contraAccount);

      TransactionType transactionType =
          transactionTypeService.findByCode(a.getTransactionTypeCode());
      if (transactionType == null)
        throw new Status414AccountingTransactionDataValidationException(
            "Invalid transaction type " + a.getTransactionTypeCode());

      if (properties.getBalanceAdjustments().isSummarizeEnabled()) {
        populateAccount(domain, currency, account, a, transactionType);
        populateAccount(domain, currency, contraAccount, a, transactionType);
      }
    }
  }

  private void populateAccount(
      Domain domain,
      Currency currency,
      Account account,
      AdjustmentRequestComponent a,
      TransactionType transactionType) {
    for (Granularity granularity : Granularity.values()) {
      Period period = periodService.findOrCreatePeriod(a.getDate(), domain, granularity.id());
      log.debug(
          "populatePeriods populated id {} granularity {} player {} period {}",
          period.getId(),
          period.getGranularity(),
          a.getOwnerGuid(),
          period);
      SummaryAccount summaryAccount = summaryAccountService.findOrCreate(period, account, false);
      log.debug(
          "summaryAccount populated id {} granularity {} player {} accountCode {} summaryAccount {}",
          summaryAccount.getId(),
          period.getGranularity(),
          a.getOwnerGuid(),
          summaryAccount.getAccount().getAccountCode().getCode(),
          summaryAccount);
      SummaryAccountTransactionType summaryTranTypeAccount =
          summaryAccountTransactionTypeService.findOrCreate(
              period, account, transactionType, false);
      log.debug(
          "summaryAccountTransactionType populated id {} granularity {} player {} transactionType {} summaryTranTypeAccount {}",
          summaryAccount.getId(),
          period.getGranularity(),
          a.getOwnerGuid(),
          transactionType.getCode(),
          summaryTranTypeAccount);
      //			SummaryAccountLabelValue summaryAccountLabelValue =
      // summaryAccountLabelValueService.findOrCreate(period, account, transactionType,
      //			log.info("summaryAccountTransactionType populated id {} granularity {} player {}
      // transactionType {} summaryTranTypeAccount {}",
      //					summaryAccount.getId(), period.getGranularity(), a.getOwnerGuid(),
      // transactionType.getCode(), summaryTranTypeAccount);
    }
  }

  private void publishEvents(AdjustmentContext context) {
    ArrayList<AdjustmentRequestComponent> adjustmentRequestList =
        context.getRequest().getAdjustments();
    // run through the completed adjustment transactions
    for (int p = 0; p < context.getTransactionStreamDataList().size(); ++p) {

      TransactionStreamData transactionStreamData = context.getTransactionStreamDataList().get(p);
      if (transactionStreamData.getTransactionId() != null) {
        log.debug("Dispatch affiliate stream data: " + transactionStreamData);
        // Send data to whoever wants to know about the adjustment completion
        transactionStream.register(transactionStreamData);

        // Retrieve the list of labels for the adjustment transaction being evaluated
        List<lithium.service.accounting.objects.LabelValue> lvList = new ArrayList<>();
        context
            .getSummaryLabelValueList()
            .get(p)
            .forEach(
                lv -> {
                  lvList.add(mapper.map(lv, lithium.service.accounting.objects.LabelValue.class));
                });

        // Send the labels to the summary processor to add the relevant labels for summary job
        AuxLabelStreamData entry = AuxLabelStreamData.builder()
                .transactionId(transactionStreamData.getTransactionId())
                .labelValueList(lvList)
                .build();
        log.debug("Register AuxLabelStreamData: " + entry);
        if (properties.getBalanceAdjustments().isSummarizeEnabled()) {
          auxLabelStream.register(entry);
        }
      }
    }
    ;

    if (properties.getBalanceAdjustments().isDispatchUserBalanceEventEnabled()
        && context.getResponse().getAdjustments().size() > 0) {
      transactionServiceWrapper.dispatchUserBalanceEvent(
          adjustmentRequestList.get(0).getOwnerGuid(),
          adjustmentRequestList.get(0).getDomainName(),
          adjustmentRequestList.get(0).getCurrencyCode());
    }
  }
}
