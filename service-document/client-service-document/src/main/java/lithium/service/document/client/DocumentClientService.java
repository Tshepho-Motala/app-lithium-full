package lithium.service.document.client;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.document.client.objects.Document;
import lithium.service.document.client.objects.DocumentInfo;
import lithium.service.document.client.objects.DocumentRequest;
import lithium.service.document.client.objects.mail.MailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class DocumentClientService {


    @Autowired
    private LithiumServiceClientFactory factory;

    public Response<Document> createDocument(String name, String statusString, String documentFunction,
                                             String ownerGuid, String authorServiceName, String authorGuid) throws Exception {
        return getClient().createDocument(name, statusString, documentFunction, ownerGuid, authorServiceName, authorGuid);
    }

    public void sendDwhNotification(MailRequest request) throws Exception {
        getClient().sendDwhNotification(request);
    }

    public Response<DocumentInfo> createAndUploadDocument(DocumentRequest documentRequest) throws Exception {
        return getClient().createAndUploadDocument(documentRequest);
    }

    private DocumentClient getClient() throws Exception {
        try {
            return factory.target(DocumentClient.class, true);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
