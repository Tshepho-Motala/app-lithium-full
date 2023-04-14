package lithium.service.document.generation.controllers;

import lithium.service.document.generation.client.clients.DataUploadClient;
import lithium.service.document.generation.client.enums.DocumentGenerationStatus;
import lithium.service.document.generation.client.objects.CsvGenerationDataResponse;
import lithium.service.document.generation.services.GenerationProcessingControlService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/processing/data")
public class GenerationDataController implements DataUploadClient {

    private GenerationProcessingControlService service;

    @Override
    @PostMapping()
    public void uploadSuccessGenerationResult(CsvGenerationDataResponse generationDataResponse) {
        service.uploadData(generationDataResponse);
        if (DocumentGenerationStatus.CANCELED.getValue() != service.getDocumentGenerationStatus(generationDataResponse.getReference()).getValue()) {
            service.uploadData(generationDataResponse);
            service.changeGenerationStatus(generationDataResponse.getReference(), DocumentGenerationStatus.COMPLETE);
        }
    }

}
