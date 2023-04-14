package lithium.service.document.generation.jobs;

import lithium.service.document.generation.config.DocumentGenerationConfigurationProperties;
import lithium.service.document.generation.data.entities.DocumentFile;
import lithium.service.document.generation.data.entities.DocumentGeneration;
import lithium.service.document.generation.data.repositories.DocumentFileRepository;
import lithium.service.document.generation.data.repositories.DocumentGenerationRepository;
import lithium.service.document.generation.data.repositories.RequestParametersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DocumentFileClearJob {
    @Autowired
    private DocumentFileRepository documentFileRepository;

    @Autowired
    private DocumentGenerationRepository documentGenerationRepository;
    @Autowired
    private DocumentGenerationConfigurationProperties properties;

    @Autowired
    private RequestParametersRepository repository;

    @Scheduled(cron = "${lithium.service.document.generation.cron:0 0 */1 * * *}")
    @Transactional
    @Modifying
    public void process() {
        long clearDelay = properties.getClearDelayMillis();
        List<DocumentFile> documents = documentFileRepository.deleteDocumentFileByCreatedDateBefore(new Date(System.currentTimeMillis() - clearDelay));

        log.info("Document generations clear job started and found: " + documents.size() + " generations for deleting");

        List<DocumentGeneration> generations = documents.stream()
                .map(DocumentFile::getReference)
                .map(Long::valueOf)
                .map(documentGenerationRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        deleteGenerationData(generations);

        log.debug("Deleted document generations files: " + documents.stream()
                .map(DocumentFile::getReference)
                .collect(Collectors.joining(","))
        );
    }

    private void deleteGenerationData(List<DocumentGeneration> content) {
        content.forEach(documentGeneration -> repository.deleteAll(documentGeneration.getParameters()));
        documentGenerationRepository.deleteAll(content);
        log.debug(content.size() + " generations deleted by cleanJob");
    }
}
