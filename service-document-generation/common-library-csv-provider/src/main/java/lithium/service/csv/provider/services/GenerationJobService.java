package lithium.service.csv.provider.services;

import com.google.common.collect.ImmutableList;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.document.generation.client.clients.DataUploadClient;
import lithium.service.document.generation.client.clients.GenerationProcessingStatusClient;
import lithium.service.document.generation.client.enums.DocumentGenerationStatus;
import lithium.service.document.generation.client.objects.CommandParams;
import lithium.service.document.generation.client.objects.CsvContent;
import lithium.service.document.generation.client.objects.CsvDataResponse;
import lithium.service.document.generation.client.objects.CsvGenerationDataResponse;
import lithium.service.document.generation.client.objects.GenerateCsvRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@AllArgsConstructor
@Service
public class GenerationJobService {
    private final List<DocumentGenerationStatus> STATUSES_NOT_ALLOWED_FOR_PROCEED = ImmutableList
            .of(DocumentGenerationStatus.COMPLETE, DocumentGenerationStatus.FAILED, DocumentGenerationStatus.DOWNLOADED);

    private final LithiumServiceClientFactory factory;
    private final CsvProviderAdapter adapter;
    private final CsvService csvService;

    public void proceedCsvGeneration(GenerateCsvRequest request) {

        log.info("Processing csv generation request :  {}", request);
        try {
            generate(request);
        } catch (Exception e) {
            log.error("Error while processing csv generation request :  {}" + e.getMessage(), request, e);
        }
    }

    private void generate(GenerateCsvRequest request) throws Exception {

        GenerationProcessingStatusClient generationStatusClient = getGenerationStatusClient();

        DocumentGenerationStatus actualStatus = generationStatusClient.getStatus(request.getReference());
        if (actualStatus == null || STATUSES_NOT_ALLOWED_FOR_PROCEED.contains(actualStatus)) {
            log.warn("Generation:" + request.getReference() + " not allowed to processing with status:" + actualStatus + " skipping");
            return;
        }

        try {
            generationStatusClient.changeDocumentGenerationStatus(request.getReference(), DocumentGenerationStatus.BUSY);

            CsvGenerationDataResponse data = collectData(generationStatusClient, request);

            uploadCollectedData(data);

        } catch (Exception e) {
            generationStatusClient.changeDocumentGenerationStatus(request.getReference(), DocumentGenerationStatus.FAILED);
            throw e;
        }
    }

    private void uploadCollectedData(CsvGenerationDataResponse data) throws LithiumServiceClientFactoryException {
        DataUploadClient uploadDataClient = getDataUploadClient();
        uploadDataClient.uploadSuccessGenerationResult(data);
        log.info("Generation:" + data.getReference() + " Successfully uploaded");
    }

    private CsvGenerationDataResponse collectData(GenerationProcessingStatusClient client, GenerateCsvRequest request) throws Exception {

        CommandParams params = adapter.buildCommandParams(validateParameters(request.getParameters()));


        List<CsvContent> collectedCSV = new ArrayList<>();
        int actualPage = 0;
        boolean hasMoreData = true;
        while (hasMoreData) {

            DocumentGenerationStatus actualStatus = client.getStatus(request.getReference());

            if (DocumentGenerationStatus.CANCELED.getValue() == actualStatus.getValue()) {
                break;
            }

            CsvDataResponse dataResponse = adapter.getCsvData(params, actualPage);
            collectedCSV.addAll(dataResponse.getData());
            actualPage++;
            hasMoreData = actualPage < dataResponse.getPages();

        }
        return buildGenerationResult(request.getReference(), collectedCSV, adapter.getContentType(request.getParameters()));
    }

    private CsvGenerationDataResponse buildGenerationResult(Long reference, List<CsvContent> collectedCSV, Class<? extends CsvContent> type) throws Exception {
        CsvGenerationDataResponse result = new CsvGenerationDataResponse(reference);
        result.setData(csvService.export(collectedCSV, type).toString().getBytes());
        result.setTotalElements(collectedCSV.size());
        log.debug("csv data for request:" + reference + " collected");

        return result;
    }

    private static Map<String, String> validateParameters(Map<String, String> parameters) {
        return parameters.entrySet().stream()
                .filter(entry -> nonNull(entry.getValue()) && !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private GenerationProcessingStatusClient getGenerationStatusClient() throws LithiumServiceClientFactoryException {
        return factory.target(GenerationProcessingStatusClient.class, true);
    }

    private DataUploadClient getDataUploadClient() throws LithiumServiceClientFactoryException {
        return factory.target(DataUploadClient.class, true);
    }
}
