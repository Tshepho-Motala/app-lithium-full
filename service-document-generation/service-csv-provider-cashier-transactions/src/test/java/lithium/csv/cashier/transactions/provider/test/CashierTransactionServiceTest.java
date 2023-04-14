package lithium.csv.cashier.transactions.provider.test;

import lithium.csv.cashier.transactions.provider.config.CsvCashierTransactionsProviderConfigurationProperties;
import lithium.csv.cashier.transactions.provider.data.CashierTransactionCsv;
import lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams;
import lithium.csv.cashier.transactions.provider.services.CashierTransactionService;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.cashier.client.objects.Domain;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.objects.User;
import lithium.service.cashier.client.objects.transaction.dto.CashierClientTransactionDTO;
import lithium.service.cashier.client.objects.transaction.dto.DomainMethod;
import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.cashier.client.objects.transaction.dto.TransactionPaymentTypeDTO;
import lithium.service.cashier.client.objects.transaction.dto.TransactionStatusDTO;
import lithium.service.cashier.client.objects.transaction.dto.TransactionWorkflowHistoryDTO;
import lithium.service.cashier.client.system.TransactionClient;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.document.generation.client.objects.CsvDataResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static lithium.csv.cashier.transactions.provider.test.CashierTransactionRequestParamsTest.getCompleteCashierParams;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CashierTransactionServiceTest {
    @Mock
    private LithiumServiceClientFactory mockedLithiumServiceClientFactory;
    @Mock
    private TransactionClient mockedClient;

    private CashierTransactionService service;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    public void init() {

        try {
            when(mockedLithiumServiceClientFactory.target(TransactionClient.class, "service-cashier", true)).thenReturn(mockedClient);
        } catch (LithiumServiceClientFactoryException e) {
            throw new RuntimeException(e);
        }
        this.service = new CashierTransactionService(new CsvCashierTransactionsProviderConfigurationProperties(), mockedLithiumServiceClientFactory);
    }

    @Test
    public void shouldBuildCorrectResponse() throws Status500InternalServerErrorException, ParseException {


        DataTableResponse<CashierClientTransactionDTO> dummyResponse = new DataTableResponse<CashierClientTransactionDTO>();
        dummyResponse.setRecordsTotalPages(1);
        dummyResponse.setData(new ArrayList<>(List.of(getDummyTransactionDto())));

        when(mockedClient.searchTransactionsByFilter(any(), any(), any())).thenReturn(dummyResponse);

        CashierTransactionRequestParams commandParams = new CashierTransactionRequestParams(getCompleteCashierParams());

        CsvDataResponse actualResponse = service.getCsvData(commandParams, 0);

        assertThat(actualResponse).usingRecursiveComparison().isEqualTo(getExpectedCsvResponse());

    }

    public static CashierClientTransactionDTO getDummyTransactionDto() throws ParseException{


        DomainMethod domainMethod = new DomainMethod();
        domainMethod.setName("testWithdrawal");
        domainMethod.setDomain(new Domain(6L, 1, "test_domain"));


        DomainMethodProcessor domainMethodProcessor = DomainMethodProcessor.builder()
                .description("some_description")
                .build();

        TransactionWorkflowHistoryDTO current = TransactionWorkflowHistoryDTO.builder()
                .processor(domainMethodProcessor)
                .status(TransactionStatusDTO.builder().code("CANCEL").id(6L).build())
                .timestamp(DATE_FORMAT.parse("1970-01-01 03:00:10"))
                .build();


        CashierClientTransactionDTO transactionDTO = CashierClientTransactionDTO.builder()
                .id(20L)
                .amountCents(10000L)
                .domainMethod(domainMethod)
                .transactionType(TransactionType.WITHDRAWAL)
                .transactionPaymentType(TransactionPaymentTypeDTO.builder().paymentType("testPaymentType").build())
                .createdOn(DATE_FORMAT.parse("1970-01-01 03:00:10"))
                .current(current)
                .user(User.builder().guid("testUserGuid").testAccount(false).build())
                .declineReason("Closed loop")
                .reviewedByFullName("Super Admin")
                .runtime("290")
                .currencyCode("NGN")
                .build();

        return transactionDTO;
    }

    public static CsvDataResponse getExpectedCsvResponse() {
        CashierTransactionCsv csv = CashierTransactionCsv.builder()
                .id("20")
                .createdOn("1970-01-01 03:00:10")
                .updatedOn("1970-01-01 03:00:10")
                .transactionType("WITHDRAWAL")
                .processorName("some_description")
                .transactionPaymentType("testPaymentType")
                .domainMethodName("testWithdrawal")
                .amount("NGN 100.00")
                .guid("testUserGuid")
                .descriptor("N/A")
                .status("CANCEL")
                .declineReason("Closed loop")
                .processorReference("")
                .additionalReference("")
                .testAccount("no")
                .autoApproved("no")
                .reviewedByFullName("Super Admin")
                .accountInfo("")
                .bonusCode("")
                .bonusId("")
                .runtime("290")
                .build();

        return CsvDataResponse.builder().data(new ArrayList<>(List.of(csv))).pages(1).build();
    }


}
