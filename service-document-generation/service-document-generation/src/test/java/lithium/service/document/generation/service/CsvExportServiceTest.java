package lithium.service.document.generation.service;

import lithium.service.document.generation.client.enums.DocumentGenerationStatus;
import lithium.service.document.generation.client.objects.CsvGenerationJob;
import lithium.service.document.generation.client.objects.CsvProvider;
import lithium.service.document.generation.client.objects.GenerateCsvRequest;
import lithium.service.document.generation.config.streams.CsvGenerationOutputStream;
import lithium.service.document.generation.config.streams.cashier.CashierCsvProviderOutputStream;
import lithium.service.document.generation.config.streams.cashier.CashierTransactionsCsvGenerationOutputQueue;
import lithium.service.document.generation.data.entities.DocumentGeneration;
import lithium.service.document.generation.data.objects.CsvGenerationStatus;
import lithium.service.document.generation.data.repositories.DocumentGenerationRepository;
import lithium.service.document.generation.data.repositories.RequestParametersRepository;
import lithium.service.document.generation.services.CsvExportService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Ignore
public class CsvExportServiceTest {

    /*private final static String DOMAIN_NAME = "Test_Domain";
    private final static String GUID = "test/guid";
    private CsvExportService service;

    @Mock
    private DocumentGenerationRepository mockedDocumentGenerationRepository;
    @Mock
    private RequestParametersRepository mockedRequestParametersRepository;
    @Mock
    private CashierTransactionsCsvGenerationOutputQueue mockedCashierTransactionsCsvGenerationOutputQueue;
    @Mock
    private MessageChannel mockedMessageChannel;

    @BeforeEach
    public void init() {
        DateTimeUtils.setCurrentMillisFixed(100000);
    }

    private void commonInit() {
        EnumMap<CsvProvider, CsvGenerationOutputStream> providerChannels = new EnumMap<>(CsvProvider.class);

        CashierCsvProviderOutputStream cashierCsvProviderOutputStream = new CashierCsvProviderOutputStream(mockedCashierTransactionsCsvGenerationOutputQueue);

        providerChannels.put(CsvProvider.CASHIER_TRANSACTION, cashierCsvProviderOutputStream);

        this.service = new CsvExportService(mockedDocumentGenerationRepository, mockedRequestParametersRepository, providerChannels.values().stream().toList());
    }

    private static DocumentGeneration buildCashierDocumentGenerationRequest() {
        DocumentGeneration savedNewGeneration = DocumentGeneration.builder()
                .id(0L)
                .createdDate(DateTime.now().toDate())
                .authorGuid(GUID)
                .provider(CsvProvider.CASHIER_TRANSACTION)
                .status(0)
                .contentType("text/csv")
                .build();
        return savedNewGeneration;
    }

    @Test
    public void shouldReturnFailedIfProviderStreamNotInitialized() {

        this.service = new CsvExportService(mockedDocumentGenerationRepository, mockedRequestParametersRepository, new ArrayList<>());

        GenerateCsvRequest cashierRequest = GenerateCsvRequest.builder()
                .domain(DOMAIN_NAME)
                .provider(CsvProvider.CASHIER_TRANSACTION)
                .build();

        CsvGenerationStatus expectedFailedResponse = CsvGenerationStatus.builder()
                .status(DocumentGenerationStatus.FAILED.name())
                .comment("ProviderOutputStream not found for:service-csv-provider-cashier-transactions")
                .build();

        CsvGenerationStatus actualResponse = service.generate(cashierRequest, GUID);

        assertThat(actualResponse).isEqualTo(expectedFailedResponse);
    }

    @Test
    public void shouldReturnOkForNewCashierTransactionRequest() {
        commonInit();

        DocumentGeneration savedNewGeneration = buildCashierDocumentGenerationRequest();

        when(mockedCashierTransactionsCsvGenerationOutputQueue.getChannel()).thenReturn(mockedMessageChannel);
        when(mockedMessageChannel.send(any())).thenReturn(true);

        when(mockedDocumentGenerationRepository.findFirstByProviderAndStatusAndAuthorGuid(CsvProvider.CASHIER_TRANSACTION, DocumentGenerationStatus.CREATED.getValue(), GUID)).thenReturn(null);
        when(mockedDocumentGenerationRepository.save(savedNewGeneration)).thenReturn(savedNewGeneration);

        GenerateCsvRequest cashierRequest = GenerateCsvRequest.builder()
                .domain(DOMAIN_NAME)
                .provider(CsvProvider.CASHIER_TRANSACTION)
                .build();

        CsvGenerationStatus acceptedSuccessResponse = CsvGenerationStatus.builder()
                .status(DocumentGenerationStatus.CREATED.name())
                .reference(0L)
                .build();

        CsvGenerationStatus actualResponse = service.generate(cashierRequest, GUID);

        assertThat(actualResponse).isEqualTo(acceptedSuccessResponse);
    }

    @Test
    public void shouldBuildCorrectStreamMessageForCashierTransactionRequest() {
        commonInit();

        DocumentGeneration savedNewGeneration = buildCashierDocumentGenerationRequest();

        when(mockedDocumentGenerationRepository.findFirstByProviderAndStatusAndAuthorGuid(CsvProvider.CASHIER_TRANSACTION, DocumentGenerationStatus.CREATED.getValue(), GUID)).thenReturn(null);
        when(mockedDocumentGenerationRepository.save(savedNewGeneration)).thenReturn(savedNewGeneration);

        ArgumentCaptor<Message> messageChannelArgumentCaptor = ArgumentCaptor.forClass(Message.class);

        when(mockedCashierTransactionsCsvGenerationOutputQueue.getChannel()).thenReturn(mockedMessageChannel);
        when(mockedMessageChannel.send(messageChannelArgumentCaptor.capture())).thenReturn(true);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("dmp", "5");
        parameters.put("dm", "6");
        parameters.put("domain", "livescore_nigeria");
        parameters.put("transactionType", "deposit");
        parameters.put("statuses", "SUCCESS");
        parameters.put("search", "2121");

        GenerateCsvRequest cashierRequest = GenerateCsvRequest.builder()
                .domain(DOMAIN_NAME)
                .provider(CsvProvider.CASHIER_TRANSACTION)
                .parameters(parameters)
                .build();

        service.generate(cashierRequest, GUID);

        CsvGenerationJob actualPayload = (CsvGenerationJob) messageChannelArgumentCaptor.getValue().getPayload();

        CsvGenerationJob acceptedJob = CsvGenerationJob.builder()
                .parameters(parameters)
                .reference(0L)
                .build();

        assertThat(actualPayload).isEqualTo(acceptedJob);
    }

    @Test
    public void shouldReturnOkForExistingCashierTransactionRequest() {
        commonInit();

        DocumentGeneration savedGeneration = buildCashierDocumentGenerationRequest();

        when(mockedDocumentGenerationRepository.findFirstByProviderAndStatusAndAuthorGuid(CsvProvider.CASHIER_TRANSACTION, DocumentGenerationStatus.CREATED.getValue(), GUID)).thenReturn(savedGeneration);

        GenerateCsvRequest cashierRequest = GenerateCsvRequest.builder()
                .domain(DOMAIN_NAME)
                .provider(CsvProvider.CASHIER_TRANSACTION)
                .build();

        CsvGenerationStatus acceptedSuccessResponse = CsvGenerationStatus.builder()
                .status(DocumentGenerationStatus.CREATED.name())
                .reference(0L)
                .build();

        CsvGenerationStatus actualResponse = service.generate(cashierRequest, GUID);

        assertThat(actualResponse).isEqualTo(acceptedSuccessResponse);
    }

    @AfterEach
    public void cleanup() {
        DateTimeUtils.setCurrentMillisSystem();
    }*/

}
