package lithium.service.document.generation.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.document.generation.data.entities.DocumentGeneration;
import lithium.service.document.generation.data.objects.DocumentGenerateBO;
import lithium.service.document.generation.data.repositories.DocumentGenerationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class GenerationsService {
    private final DocumentGenerationRepository repository;
    private final ObjectMapper objectMapper;
    public DataTableResponse<DocumentGenerateBO> findUserGenerations(String guid, DataTableRequest request) {
        Page<DocumentGeneration> generationPage = repository.findAllByAuthorGuid(guid, request.getPageRequest());
        Page<DocumentGenerateBO> mapped = generationPage.map(this::convert);
        return new DataTableResponse<>(request, mapped);
    }

    private DocumentGenerateBO convert(DocumentGeneration documentGeneration) {
        return objectMapper.convertValue(documentGeneration, DocumentGenerateBO.class);
    }
}
