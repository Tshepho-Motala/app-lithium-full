package lithium.service.document.controllers;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.document.client.objects.DocumentFile;
import lithium.service.document.client.objects.DocumentInfo;
import lithium.service.document.client.objects.DocumentRequest;
import lithium.service.document.client.objects.mail.MailRequest;
import lithium.service.document.config.ServiceDocumentConfigurationProperties;
import lithium.service.document.data.entities.Document;
import lithium.service.document.data.entities.File;
import lithium.service.document.services.DocumentService;
import lithium.service.document.services.DwhNotificationService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@EnableConfigurationProperties(ServiceDocumentConfigurationProperties.class)
@Slf4j
@RestController
@RequestMapping("/document")
// no tight coupling between client and service (so no implementation or
// dependence on service-client
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DwhNotificationService dwhNotificationService;

    @Retryable(backoff = @Backoff(delay = 500), maxAttempts = 10)
    @RequestMapping(value = "/saveFile/{documentUuid}", method = RequestMethod.POST)
    public ResponseEntity<DocumentFile> saveFile(@PathVariable("documentUuid") String documentUuid,
                                                 @RequestPart("file") MultipartFile mpFile,
                                                 @RequestPart("authorGuid") String authorGuid,
                                                 @RequestPart(name = "documentStatus", required = false) String documentStatus,
                                                 LithiumTokenUtil lithiumTokenUtil) throws Exception {

        log.info("Received a document: " + documentUuid);

        Response<lithium.service.document.data.entities.DocumentFile> response = documentService.saveFile(documentUuid, mpFile.getBytes(), mpFile.getContentType(), mpFile.getOriginalFilename(), authorGuid, documentStatus, 1, lithiumTokenUtil);
        return new ResponseEntity<DocumentFile>(documentService.mapWithUuid(documentUuid, response.getData()), response.getStatus() == Status.OK ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    public Response<DocumentFile> downloadFile(@RequestParam("documentUuid") String documentUuid, @RequestParam(name = "page", defaultValue = "1", required = false) Integer page) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noStore().getHeaderValue());

        Response<DocumentFile> responseDoc = documentService.findV1FileByDocumentUuid(documentUuid, page);

        return responseDoc;
    }

    @Retryable(backoff = @Backoff(delay = 500), maxAttempts = 10)
    @RequestMapping("/createDocument")
    public Response<Document> createDocument(@RequestParam("name") String name,
                                             @RequestParam("statusString") String statusString,
                                             @RequestParam("documentFunction") String documentFunction,
                                             @RequestParam("ownerGuid") String ownerGuid,
                                             @RequestParam("authorServiceName") String authorServiceName,
                                             @RequestParam("authorGuid") String authorGuid) throws Exception {

        Response<Document> response = Response.<Document>builder()
                .data(documentService.createDocument(name, statusString, documentFunction, ownerGuid, authorServiceName, authorGuid))
                .status(Status.OK).build();

        return response;
    }

    @Retryable(backoff = @Backoff(delay = 500), maxAttempts = 10)
    @RequestMapping("/updateDocument")
    public Response<Document> updateDocument(@RequestParam("documentUuid") String documentUuid,
                                             @RequestParam(required = false, name = "name") String name,
                                             @RequestParam(required = false, name = "statusString") String statusString,
                                             @RequestParam(required = false, name = "documentFunction") String documentFunction,
                                             @RequestParam(required = false, name = "archive") boolean archive,
                                             @RequestParam(required = false, name = "delete") boolean delete,
                                             @RequestParam("authorGuid") String authorGuid) throws Exception {

        Response<Document> response = Response.<Document>builder()
                .data(documentService.updateDocument(name, documentUuid, statusString, documentFunction, archive, delete, authorGuid))
                .status(Status.OK).build();

        return response;
    }

    @Retryable(backoff = @Backoff(delay = 500), maxAttempts = 10)
    @RequestMapping("/findDocumentByUuid")
    public Response<Document> findDocumentByUuid(String uuid) throws Exception {


        return null;
    }

    @Retryable(backoff = @Backoff(delay = 500), maxAttempts = 10)
    @RequestMapping("/findDocumentByOwnerAndAuthorService")
    public Response<List<lithium.service.document.client.objects.Document>> findDocumentByOwnerAndAuthorService(@RequestParam("ownerGuid") String ownerGuid, @RequestParam("authorServiceName") String authorServiceName, @RequestParam("authorGuid") String authorGuid) throws Exception {

        return documentService.findDocumentByOwnerAndAuthorService(ownerGuid, authorServiceName, authorGuid);
    }

    @Retryable(backoff = @Backoff(delay = 500), maxAttempts = 10)
    @RequestMapping("/findDocumentByOwnerAndAuthorServiceAndFunction")
    public Response<Document> findDocumentByOwnerAndAuthorServiceAndFunction(String ownerGuid, String authorServiceName, String documentFunction, @RequestParam("authorGuid") String authorGuid) throws Exception {


        return null;
    }

    @Retryable(backoff = @Backoff(delay = 500), maxAttempts = 10)
    @RequestMapping("/findFileByDocumentUuid")
    public Response<File> findFileByDocumentUuid(@RequestParam("documentUuid") String documentUuid, @RequestParam("authorGuid") String authorGuid) throws Exception {


        return null;
    }

    @RequestMapping(value = "/sendDwhNotification", method = RequestMethod.POST)
    public ResponseEntity sendDwhNotification(@RequestBody MailRequest request) throws Exception {

        dwhNotificationService.sendMail(request);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/createAndUploadDocument")
    public Response<DocumentInfo> createAndUploadDocument(@RequestBody DocumentRequest documentRequest, LithiumTokenUtil tokenUtil) throws Exception {

        return Response.<DocumentInfo>builder()
                .data(documentService.createAndUploadDocument(documentRequest, tokenUtil)).status(Status.OK).build();
    }
}
