package lithium.csv.cashier.transactions.provider.services;


import lithium.csv.cashier.transactions.provider.config.CsvCashierTransactionsProviderConfigurationProperties;
import lithium.csv.cashier.transactions.provider.data.CashierTransactionCsv;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.service.cashier.client.objects.TransactionFilterRequest;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.objects.transaction.dto.CashierClientTransactionDTO;
import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.client.objects.transaction.dto.ProcessorUserCardDTO;
import lithium.service.cashier.client.objects.transaction.dto.TransactionPaymentTypeDTO;
import lithium.service.cashier.client.objects.transaction.dto.TransactionStatusDTO;
import lithium.service.cashier.client.system.TransactionClient;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.csv.provider.services.CsvProviderAdapter;
import lithium.service.document.generation.client.objects.CommandParams;
import lithium.service.document.generation.client.objects.CsvContent;
import lithium.service.document.generation.client.objects.CsvDataResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@AllArgsConstructor
public class CashierTransactionService implements CsvProviderAdapter<CashierTransactionRequestParams> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private CsvCashierTransactionsProviderConfigurationProperties properties;
    private LithiumServiceClientFactory lithiumServiceClientFactory;

    @Override
    public Class<? extends CsvContent> getContentType() {
        return CashierTransactionCsv.class;
    }
    @Override
    public Class<? extends CsvContent> getContentType(Map<String, String> parameters) {
        return getContentType();
    }

    @Override
    public CommandParams buildCommandParams(Map<String, String> paramsMap) {
        return new CashierTransactionRequestParams(paramsMap);
    }

    @Override
    public CsvDataResponse getCsvData(CashierTransactionRequestParams params, int page) throws Status500InternalServerErrorException {

        TransactionFilterRequest filter = params.buildTransactionFilter();
        try {
            return getCashierData(filter, page, properties.getProcessingJobPageSize());
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Can't proceed csv data:" + e.getMessage());
            throw new Status500InternalServerErrorException(e.getMessage(), e.fillInStackTrace());
        }
    }

    private CsvDataResponse getCashierData(TransactionFilterRequest filter, int page, int size) throws LithiumServiceClientFactoryException {

        TransactionClient client = lithiumServiceClientFactory.target(TransactionClient.class, "service-cashier", true);

        DataTableResponse<CashierClientTransactionDTO> response = client.searchTransactionsByFilter(filter, page, size);
        List<CashierTransactionCsv> transactionCSVs = collectCSVData(response.getData());

        return new CsvDataResponse(transactionCSVs, response.getRecordsTotalPages());

    }

    private List<CashierTransactionCsv> collectCSVData(List<CashierClientTransactionDTO> transactions) {

        return transactions.stream()
                .map(t -> CashierTransactionCsv.builder()
                        .id(t.getId().toString())
                        .createdOn(DATE_FORMAT.format(t.getCreatedOn()))
                        .updatedOn(DATE_FORMAT.format(t.getCurrent().getTimestamp()))
                        .transactionType(Optional.ofNullable(t.getTransactionType())
                                .map(TransactionType::name)
                                .orElse(""))
                        .processorName(ofNullable(t.getCurrent().getProcessor())
                                .map(DomainMethodProcessor::getDescription)
                                .orElse(""))
                        .domainMethodName(t.getDomainMethod().getName())
                        .transactionPaymentType(ofNullable(t.getTransactionPaymentType())
                                .map(TransactionPaymentTypeDTO::getPaymentType)
                                .orElse(""))
                        .amount(t.getCurrencyCode() + " " + CurrencyAmount.fromCents(ofNullable(t.getAmountCents()).orElse(0L)).toAmount().setScale(2))
                        .guid(t.getUser().getGuid())
                        .descriptor(ofNullable(t.getPaymentMethod())
                                .map(ProcessorUserCardDTO::getLastFourDigits)
                                .orElse("N/A"))
                        .status(ofNullable(t.getCurrent().getStatus())
                                .map(TransactionStatusDTO::getCode)
                                .orElse(""))
                        .declineReason(ofNullable(t.getDeclineReason())
                                .map(reason -> reason.replaceAll(",", ""))
                                .orElse(""))
                        .processorReference(ofNullable(t.getProcessorReference()).orElse(""))
                        .additionalReference(ofNullable(t.getAdditionalReference()).orElse(""))
                        .testAccount(t.getUser().isTestAccount() ? "yes" : "no")
                        .autoApproved(t.isAutoApproved() ? "yes" : "no")
                        .reviewedByFullName(ofNullable(t.getReviewedByFullName()).orElse(""))
                        .accountInfo(ofNullable(t.getAccountInfo()).orElse(""))
                        .bonusCode(ofNullable(t.getBonusCode()).orElse(""))
                        .bonusId(ofNullable(t.getBonusId())
                                .map(Object::toString)
                                .orElse(""))
                        .runtime(ofNullable(t.getRuntime()).orElse(""))
                        .build())
                .toList();
    }
}
