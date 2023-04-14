package lithium.service.accounting.provider.internal.services;

import lithium.exceptions.Status415NegativeBalanceException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.accounting.objects.ConstraintValidation;
import lithium.service.accounting.objects.TransactionStreamData;
import lithium.service.accounting.provider.internal.context.adjust.AdjustmentContext;
import lithium.service.accounting.provider.internal.data.entities.LabelValue;
import lithium.service.accounting.provider.internal.events.BalanceAdjustEvent;
import org.joda.time.DateTime;

import java.util.List;

public interface ITransactionServiceWrapper {

    Response<AdjustmentTransaction> adjustMultiInternal(
            AdjustmentContext context,
            boolean forceFlushAndClear,
            Long amountCents,
            DateTime date,
            String accountCode,
            String accountTypeCode,
            String transactionTypeCode,
            String contraAccountCode,
            String contraAccountTypeCode,
            String[] labels,
            String currencyCode,
            String domainName,
            String ownerGuid,
            String authorGuid,
            Boolean allowNegativeAdjust,
            String[] negAdjProbeAccCodes,
            TransactionStreamData transactionStreamData,
            List<LabelValue> summaryLabelValues,
            BalanceAdjustEvent evt,
            boolean parentHandlesEvent,
            Long accountId,
            List<ConstraintValidation> constraintValidations
    ) throws
      Status414AccountingTransactionDataValidationException,
      Status415NegativeBalanceException, Status500InternalServerErrorException;

    void adjustMultiBatchInternal(
            AdjustmentContext context
    ) throws
      Status414AccountingTransactionDataValidationException,
      Status415NegativeBalanceException, Status500InternalServerErrorException;
}
