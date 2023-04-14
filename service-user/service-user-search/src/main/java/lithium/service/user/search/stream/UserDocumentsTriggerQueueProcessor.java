package lithium.service.user.search.stream;

import lithium.service.user.client.objects.UserDocumentData;
import lithium.service.user.search.services.document.UserDocumentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableBinding(UserDocumentsTriggerQueueSink.class)
public class UserDocumentsTriggerQueueProcessor {
  @Autowired
  private UserDocumentsService service;

  @StreamListener(UserDocumentsTriggerQueueSink.INPUT)
  public void trigger(UserDocumentData data) {
    log.debug("Received an user-document trigger from the queue for processing: " + data);

    service.processUserDocuments(data);
  }
}
