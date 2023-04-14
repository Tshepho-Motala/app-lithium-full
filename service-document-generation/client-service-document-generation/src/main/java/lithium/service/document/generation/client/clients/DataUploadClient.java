package lithium.service.document.generation.client.clients;

import lithium.service.document.generation.client.objects.CsvGenerationDataResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "service-document-generation")
public interface DataUploadClient {

    @PostMapping(path = "/processing/data")
    void uploadSuccessGenerationResult(@RequestBody CsvGenerationDataResponse generationDataResponse);
}
