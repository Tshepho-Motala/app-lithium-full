package lithium.service.document.generation.controllers;

import lithium.service.document.generation.client.clients.GenerationProcessingStatusClient;
import lithium.service.document.generation.client.enums.DocumentGenerationStatus;
import lithium.service.document.generation.data.objects.CsvGenerationStatus;
import lithium.service.document.generation.services.GenerationProcessingControlService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/processing/status/{id}")
public class DocumentGenerationStatusController implements GenerationProcessingStatusClient {
    private GenerationProcessingControlService service;


    @Override
    @GetMapping
    public DocumentGenerationStatus getStatus(@PathVariable Long id) {
        CsvGenerationStatus generation = service.getGenerationStatus(id);
        return DocumentGenerationStatus.fromName(generation.getStatus());
    }

    @Override
    @PutMapping
    public void changeDocumentGenerationStatus(@PathVariable Long id, @RequestBody DocumentGenerationStatus newStatus) {
        service.changeGenerationStatus(id, newStatus);
    }
}
