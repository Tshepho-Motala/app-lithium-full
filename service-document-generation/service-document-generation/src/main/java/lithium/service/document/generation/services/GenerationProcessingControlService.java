package lithium.service.document.generation.services;

import java.util.Optional;
import lithium.service.document.generation.client.enums.DocumentGenerationStatus;
import lithium.service.document.generation.client.objects.CsvGenerationDataResponse;
import lithium.service.document.generation.data.entities.DocumentFile;
import lithium.service.document.generation.data.entities.DocumentGeneration;
import lithium.service.document.generation.data.objects.CsvGenerationStatus;
import lithium.service.document.generation.data.repositories.DocumentFileRepository;
import lithium.service.document.generation.data.repositories.DocumentGenerationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
public class GenerationProcessingControlService {

    private final DocumentFileRepository documentFileRepository;

    private final DocumentGenerationRepository documentGenerationRepository;

    public ResponseEntity<Resource> download(Long id) throws IOException {
        CsvGenerationStatus status = getGenerationStatus(id);

        if (status == null) {
            changeGenerationStatus(id, DocumentGenerationStatus.FAILED);
            log.warn("Status for:" + id + " not found. Status set to FAILED !!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if (DocumentGenerationStatus.COMPLETE.name().equalsIgnoreCase(status.getStatus())) {
            return getResourceResponse(id);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    private ResponseEntity<Resource> getResourceResponse(Long id) {
        DocumentFile documentFile = documentFileRepository.findDocumentFileByReference(String.valueOf(id));

        if (documentFile == null || documentFile.getData() == null) {
            changeGenerationStatus(id, DocumentGenerationStatus.FAILED);
            log.warn("Document file for:" + id + " not found. While status is COMPLETED. Status set to FAILED !!");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        byte[] data = documentFile.getData();

        ByteArrayResource resource = new ByteArrayResource(data);

        changeGenerationStatus(id, DocumentGenerationStatus.DOWNLOADED);

        String fileName = buildFileName(id);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".csv");

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(data.length)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(resource);
    }

    private String buildFileName(Long id) {
        DocumentGeneration generation = documentGenerationRepository.findById(id).get();
        StringBuilder sb = new StringBuilder();
        sb.append(generation.getSize()).append("_");
        sb.append(generation.getProvider().name()).append("_");
        sb.append(generation.getCompletedDate().toString()).append("_");
        sb.append(id);
        return sb.toString();
    }

    public CsvGenerationStatus getGenerationStatus(Long id) {
        Optional<DocumentGeneration> optionalDocumentGeneration = documentGenerationRepository.findById(id);
        DocumentGeneration generation = (optionalDocumentGeneration.isEmpty())?null:optionalDocumentGeneration.get();

        if (generation == null) {
            return null;
        }

        Integer queuePosition = getQueuePosition(generation);

        return CsvGenerationStatus.builder()
                .status(DocumentGenerationStatus.fromValue(generation.getStatus()).name())
                .reference(id)
                .comment(generation.getComment())
                .queuePosition(queuePosition)
                .build();
    }

    private Integer getQueuePosition(DocumentGeneration generation) {
        List<Integer> inProgressStatuses = List.of(DocumentGenerationStatus.CREATED.getValue(), DocumentGenerationStatus.BUSY.getValue());
        return documentGenerationRepository.findByStatusInAndProviderAndCreatedDateBefore(inProgressStatuses, generation.getProvider(), generation.getCreatedDate()).size();
    }

    public void changeGenerationStatus(Long id, DocumentGenerationStatus status) {
        DocumentGeneration generation = documentGenerationRepository.findById(id).get();
        generation.setStatus(status.getValue());
        documentGenerationRepository.save(generation);
        log.info("Changed status for: " + status.name() + " for generation:" + id);
    }

    public DocumentFile uploadData(CsvGenerationDataResponse response) {
        DocumentGeneration generation = documentGenerationRepository.findById(response.getReference()).get();
        generation.setSize(response.getTotalElements());
        generation.setCompletedDate(new Date());
        generation = documentGenerationRepository.save(generation);
        DocumentFile documentFile = documentFileRepository.findDocumentFileByReference(String.valueOf(generation.getId()));
        if (documentFile == null) {
            documentFile = DocumentFile.builder()
                    .reference(String.valueOf(generation.getId()))
                    .provider(generation.getProvider())
                    .createdDate(DateTime.now().toDate())
                    .data(response.getData())
                    .build();
        } else {
            documentFile.setData(response.getData());
        }
        return documentFileRepository.save(documentFile);
    }

    public DocumentGenerationStatus getDocumentGenerationStatus(Long id) {
        return DocumentGenerationStatus.fromValue(documentGenerationRepository.findById(id).get().getStatus());
    }
}
