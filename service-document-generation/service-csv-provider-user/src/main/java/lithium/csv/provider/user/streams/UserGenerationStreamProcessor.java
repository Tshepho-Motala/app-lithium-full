package lithium.csv.provider.user.streams;

import lithium.csv.provider.user.services.UserDataGeneration;
import lithium.service.document.generation.client.objects.GenerateCsvRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableBinding(CsvGenerationProcessingQueueSink.class)
public class UserGenerationStreamProcessor {
    private final UserDataGeneration userDataGeneration;

    @StreamListener(CsvGenerationProcessingQueueSink.INPUT)
    public void processUserGeneration(GenerateCsvRequest request) {
        userDataGeneration.proceedCsvGeneration(request);
    }
}
