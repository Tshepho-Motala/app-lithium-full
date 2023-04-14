package lithium.service.cashier.services;

import lithium.math.CurrencyAmount;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.CachingDomainClientService;

import lithium.service.exception.ResponseErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASHIER_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASHIER_BILLING_DESCRIPTOR;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASHIER_PAYMENT_DESCRIPTOR;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASHIER_PROCESSOR_METHOD;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASHIER_PROCESSOR_REFERENCE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASHIER_TRANSACTION_CURRENCY_CODE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASHIER_TRANSACTION_FEE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASHIER_TRANSACTION_ID;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASHIER_TRANSACTION_TYPE;


@Service
@Slf4j
public class CashierPlaceholderService {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CachingDomainClientService cachingDomainClientService;

    public Set<Placeholder> buildForTransactionId(Long transactionId) throws ResponseErrorException {
        Transaction transaction = transactionService.findById(transactionId);

        if (transaction == null) {
            String message = "Can't build placeholders for transactionId:" + transactionId + " transaction not found";
            log.error(message);
            throw new ResponseErrorException("Transaction not found", message);
        }
        return buildForTransaction(transaction);
    }

    public Set<Placeholder> buildForTransaction(Transaction transaction) {
        Set<Placeholder> placeholders = new HashSet<>();
        placeholders.add(CASHIER_TRANSACTION_ID.from(transaction.getId()));
        placeholders.add(CASHIER_TRANSACTION_TYPE.from(transaction.getTransactionType().name()));
        placeholders.add(CASHIER_AMOUNT.from(transactionService.getData(transaction, "amount", 1, false)));
        placeholders.add(CASHIER_TRANSACTION_CURRENCY_CODE.from(transaction.getCurrencyCode()));
        placeholders.add(CASHIER_PROCESSOR_METHOD.from(transaction.getDomainMethod().getName()));
        placeholders.add(CASHIER_PROCESSOR_REFERENCE.from(transaction.getProcessorReference()));

        String transactionFee = "-";
        if (transaction.getFeeCents() != null && transaction.getFeeCents() > 0) {
            transactionFee = getCurrencyFormatter(cachingDomainClientService.domainLocale(transaction.getDomainMethod().getDomain().getName()))
                    .format(CurrencyAmount.fromCents(transaction.getFeeCents()).toAmount());
        }
        placeholders.add(CASHIER_TRANSACTION_FEE.from(transactionFee));

        placeholders.add(CASHIER_BILLING_DESCRIPTOR.from(transactionService.getBillingDescriptor(transaction).orElse("-")));
        placeholders.add(CASHIER_PAYMENT_DESCRIPTOR.from(Optional.of(transaction).map(Transaction::getPaymentMethod).map(ProcessorUserCard::getLastFourDigits)));
        return placeholders;
    }

    private NumberFormat getCurrencyFormatter(String langTag) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag(langTag));
    }
}
