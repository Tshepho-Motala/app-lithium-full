package lithium.service.user.search.services.document;

import lithium.service.user.client.objects.UserDocumentData;
import lithium.service.user.search.data.entities.Document;
import lithium.service.user.search.data.entities.DocumentStatus;
import lithium.service.user.search.data.entities.User;
import lithium.service.user.search.data.repositories.user_search.DocumentStatusesRepository;
import lithium.service.user.search.data.repositories.user_search.DocumentsRepository;
import lithium.service.user.search.services.user_search.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Slf4j
@Service
public class UserDocumentsService {

  @Autowired
  @Qualifier("user_search.UserService")
  private UserService userService;

  @Autowired
  private DocumentsRepository documentsRepository;

  @Autowired
  private DocumentStatusesRepository documentStatusesRepository;

  public void processUserDocuments(UserDocumentData data) {
    try {
      User user = userService.findOrCreateUser(data.getGuid());
      DocumentStatus status = findOrCreateDocumentStatus(data);
      Document document = findOrCreateDocument(data.getDocumentId(), user, status);
      if (data.isDeleted()) {
        documentsRepository.deleteById(document.getId());
      } else {
        // update
        document.setStatus(status);
        document.setSensitive(data.isSensitive());
        documentsRepository.save(document);
      }
    } catch (Exception ex) {
      log.error("Cant update document data: " + data,  ex);
    }
  }

  private Document findOrCreateDocument(long id, User user, DocumentStatus status) {
    return Optional.ofNullable(documentsRepository.findById(id))
        .orElseGet(() -> documentsRepository.save(Document.builder()
        .id(id)
        .user(user)
        .status(status)
        .sensitive(false)
        .deleted(false)
        .build()));
  }

  private DocumentStatus findOrCreateDocumentStatus(UserDocumentData data) {
    return Optional.ofNullable(documentStatusesRepository.findById(data.getStatusId()))
        .orElseGet(() -> documentStatusesRepository.save(
        DocumentStatus.builder()
            .id(data.getStatusId())
            .name(data.getStatusName())
            .build()));
  }
}
