package lithium.csv.casino.provider.services;

import lithium.csv.casino.provider.config.CsvGenerationProcessingQueueSink;
import lithium.service.csv.provider.services.GenerationJobService;
import lithium.service.document.generation.client.objects.GenerateCsvRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
@EnableBinding(CsvGenerationProcessingQueueSink.class)
public class CasinoProcessor {
    private final GenerationJobService generationJobService;

    @StreamListener(CsvGenerationProcessingQueueSink.INPUT)
    public void generateCsv(GenerateCsvRequest request) {
        generationJobService.proceedCsvGeneration(request);
    }
}
