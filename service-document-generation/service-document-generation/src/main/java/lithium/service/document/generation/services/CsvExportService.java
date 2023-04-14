package lithium.service.document.generation.services;

import lithium.service.document.generation.client.enums.DocumentGenerationStatus;
import lithium.service.document.generation.client.objects.CsvGenerationJob;
import lithium.service.document.generation.client.objects.CsvProvider;
import lithium.service.document.generation.client.objects.GenerateCsvRequest;
import lithium.service.document.generation.config.streams.CsvGenerationOutputStream;
import lithium.service.document.generation.data.entities.DocumentGeneration;
import lithium.service.document.generation.data.entities.RequestParameters;
import lithium.service.document.generation.data.objects.CsvGenerationStatus;
import lithium.service.document.generation.data.repositories.DocumentGenerationRepository;
import lithium.service.document.generation.data.repositories.RequestParametersRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CsvExportService {

    private final DocumentGenerationRepository documentGenerationRepository;
    private final RequestParametersRepository requestParametersRepository;
    private final EnumMap<CsvProvider, CsvGenerationOutputStream> providerChannels;

    public CsvExportService(DocumentGenerationRepository documentGenerationRepository, RequestParametersRepository requestParametersRepository, List<CsvGenerationOutputStream> outputQueueProviders) {
        this.documentGenerationRepository = documentGenerationRepository;
        this.requestParametersRepository = requestParametersRepository;
        this.providerChannels = outputQueueProviders.stream().collect(toEnumMap());
    }

    public CsvGenerationStatus generate(GenerateCsvRequest generateCsvRequest, String authorGuid) {

        if (providerChannels.get(generateCsvRequest.getProvider()) == null) {
            log.error("ProviderOutputStream not found for:" + generateCsvRequest.getProvider().key());
            return CsvGenerationStatus.builder()
                    .status(DocumentGenerationStatus.FAILED.name())
                    .comment("ProviderOutputStream not found for:" + generateCsvRequest.getProvider().key())
                    .build();
        }

        DocumentGenerationStatus status = DocumentGenerationStatus.CREATED;
        checkRequiredParams(generateCsvRequest);

        DocumentGeneration documentGeneration = documentGenerationRepository.findFirstByProviderAndStatusAndAuthorGuid(generateCsvRequest.getProvider(), status.getValue(), authorGuid);

        if (documentGeneration == null) {
            try {
                documentGeneration = createNewGeneration(generateCsvRequest, authorGuid, status);

                Map<String, String> validatedParams = validateParams(generateCsvRequest.getParameters());
                saveParams(documentGeneration, validatedParams);

                CsvGenerationJob jobRequest = buildJobRequest(documentGeneration, validatedParams);

                addGenerationRequestToQueue(documentGeneration.getProvider(), jobRequest);
            } catch (Exception e) {
                log.error("Error while creating new generation:", e);
                return markGenerationAsFailed(documentGeneration, e.getLocalizedMessage());
            }
        }

        return CsvGenerationStatus.builder()
                .status(status.name())
                .reference(documentGeneration.getId())
                .build();
    }

    private CsvGenerationStatus markGenerationAsFailed(DocumentGeneration documentGeneration, String reason) {
        reason = "Generation failed Try to use other filters: " + reason;
        DocumentGenerationStatus newStatus = DocumentGenerationStatus.FAILED;

        if (documentGeneration != null) {
            documentGeneration.setStatus(newStatus.getValue());
            documentGeneration.setComment(reason);
            documentGenerationRepository.save(documentGeneration);
        }

        return CsvGenerationStatus.builder()
                .status(newStatus.name())
                .reference(Optional.ofNullable(documentGeneration).map(DocumentGeneration::getId).orElse(null))
                .comment(reason)
                .build();
    }

    private DocumentGeneration createNewGeneration(GenerateCsvRequest generateCsvRequest, String authorGuid, DocumentGenerationStatus status) {
        return documentGenerationRepository.save(DocumentGeneration.builder()
                .provider(generateCsvRequest.getProvider())
                .contentType("text/csv")
                .createdDate(DateTime.now().toDate())
                .status(status.getValue())
                .authorGuid(authorGuid)
                .build());
    }

    private void saveParams(DocumentGeneration documentGeneration, Map<String, String> validatedParams) {
        List<RequestParameters> requestParameters = parseParameters(validatedParams, documentGeneration);
        requestParametersRepository.saveAll(requestParameters);
    }

    private static Map<String, String> validateParams(Map<String, String> parameters) {
        Map<String, String> result = new HashMap<>();
        if (parameters == null) {
            return result;
        }
        parameters.keySet().forEach(key -> checkAndAddParam(parameters, key, result));

        return result;
    }

    private static void checkAndAddParam(Map<String, String> parameters, String key, Map<String, String> result) {
        if (parameters.get(key) != null && !parameters.get(key).isEmpty()) {
            result.put(key, parameters.get(key));
        }
    }

    private void addGenerationRequestToQueue(CsvProvider csvProvider, CsvGenerationJob request) {
        log.debug("Adding to >" + csvProvider.name() + "<: " + request.getReference());
        providerChannels.get(csvProvider).getChannel().send(MessageBuilder.withPayload(request).build());
    }

    private static CsvGenerationJob buildJobRequest(DocumentGeneration documentGeneration, Map<String, String> parameters) {
        return CsvGenerationJob.builder()
                .reference(documentGeneration.getId())
                .parameters(parameters)
                .build();
    }

    private static void checkRequiredParams(GenerateCsvRequest generateCsvRequest) {
        Map<String, String> params = generateCsvRequest.getParameters();
        if (params == null) {
            return;
        }
        if (params.get("domain") == null) {
            params.put("domain", generateCsvRequest.getDomain());
            generateCsvRequest.setParameters(params);
        }
    }

    private static List<RequestParameters> parseParameters(Map<String, String> parametersMap, DocumentGeneration documentGeneration) {
        if (parametersMap == null) {
            return new ArrayList<>();
        }
        return parametersMap.entrySet()
                .stream()
                .map(entry -> RequestParameters.builder()
                        .key(entry.getKey())
                        .value(entry.getValue())
                        .generation(documentGeneration)
                        .build())
                .toList();
    }

    private static <S extends CsvGenerationOutputStream> Collector<S, ?, EnumMap<CsvProvider, S>> toEnumMap() {
        return Collectors.toMap(CsvGenerationOutputStream::getProvider, Function.identity(), throwingMerger(), () -> new EnumMap<>(CsvProvider.class));
    }

    private static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }
}
