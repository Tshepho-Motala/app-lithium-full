package lithium.service.document.generation.client.clients;


import lithium.service.document.generation.client.enums.DocumentGenerationStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "service-document-generation")
public interface GenerationProcessingStatusClient {

    @GetMapping(path = "/processing/status/{id}")
    DocumentGenerationStatus getStatus(@PathVariable Long id);

    @PutMapping(path = "/processing/status/{id}")
    void changeDocumentGenerationStatus(@PathVariable Long id, @RequestBody DocumentGenerationStatus newStatus);

}
