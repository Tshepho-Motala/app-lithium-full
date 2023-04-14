package lithium.service.document.client;

import lithium.service.Response;
import lithium.service.document.client.objects.Document;
import lithium.service.document.client.objects.DocumentFile;
import lithium.service.document.client.objects.DocumentInfo;
import lithium.service.document.client.objects.DocumentRequest;
import lithium.service.document.client.objects.mail.MailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

@FeignClient(name = "service-document")
public interface DocumentClient {

    @RequestMapping("/document/findDocumentByOwnerAndAuthorService")
    public Response<List<Document>> findDocumentByOwnerAndAuthorService(@RequestParam("ownerGuid") String ownerGuid, @RequestParam("authorServiceName") String authorServiceName, @RequestParam("authorGuid") String authorGuid) throws Exception;

    @RequestMapping("/document/findFileByDocumentUuid")
    public Response<DocumentFile> findFileByDocumentUuid(@RequestParam("documentUuid") String documentUuid, @RequestParam("authorGuid") String authorGuid) throws Exception;

    @RequestMapping(value = "/document/downloadFile", method = RequestMethod.GET)
    public Response<lithium.service.document.client.objects.DocumentFile> downloadFile(@RequestParam("documentUuid") String documentUuid, @RequestParam(name = "page", defaultValue = "1", required = false) Integer page) throws Exception;


    @RequestMapping(value = "/document/createDocument")
    public Response<Document> createDocument(@RequestParam("name") String name,
                                             @RequestParam("statusString") String statusString,
                                             @RequestParam("documentFunction") String documentFunction,
                                             @RequestParam("ownerGuid") String ownerGuid,
                                             @RequestParam("authorServiceName") String authorServiceName,
                                             @RequestParam("authorGuid") String authorGuid) throws Exception;

    @RequestMapping(value = "/document/updateDocument")
    public Response<Document> updateDocument(@RequestParam("documentUuid") String documentUuid,
                                             @RequestParam(required = false, name = "name") String name,
                                             @RequestParam(required = false, name = "statusString") String statusString,
                                             @RequestParam(required = false, name = "documentFunction") String documentFunction,
                                             @RequestParam(required = false, name = "archive") boolean archive,
                                             @RequestParam(required = false, name = "delete") boolean delete,
                                             @RequestParam("authorGuid") String authorGuid) throws Exception;

    @RequestMapping(value = "/document/sendDwhNotification", method = RequestMethod.POST)
    public ResponseEntity sendDwhNotification(@RequestBody MailRequest request) throws Exception;

    @RequestMapping(value = "/document/createAndUploadDocument")
    public Response<DocumentInfo> createAndUploadDocument(@RequestBody DocumentRequest documentRequest) throws Exception;
}

