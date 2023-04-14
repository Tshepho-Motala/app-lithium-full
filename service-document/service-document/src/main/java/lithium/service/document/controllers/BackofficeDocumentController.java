package lithium.service.document.controllers;

import lithium.service.Response;
import lithium.service.document.client.objects.DocumentInfo;
import lithium.service.document.data.entities.DocumentFile;
import lithium.service.document.data.objects.TextValue;
import lithium.service.document.exceptions.Status404DocumentNotFoundException;
import lithium.service.document.services.DocumentTypeService;
import lithium.service.document.services.DocumentV2Service;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/backoffice/document/{domain}")
public class BackofficeDocumentController {

    @Autowired
    private DocumentV2Service documentService;
    @Autowired
    private DocumentTypeService documentTypeService;

    @GetMapping("/per-user")
    public Response<List<DocumentInfo>> list(@PathVariable("domain") String domain, @RequestParam("ownerGuid") String ownerGuid, LithiumTokenUtil tokenUtil) {
        return Response.<List<DocumentInfo>>builder().data(documentService.listPerUser(ownerGuid, false)).build();
    }

    @GetMapping("/per-user-sensitive")
    public Response<List<DocumentInfo>> listWithSensitive(@PathVariable("domain") String domain, @RequestParam("ownerGuid") String ownerGuid, LithiumTokenUtil tokenUtil) {
        return Response.<List<DocumentInfo>>builder().data(documentService.listPerUser(ownerGuid, true)).build();
    }

    @GetMapping("/get-document-file")
    public Response<DocumentFile> getDocumentFile(@PathVariable("domain") String domain, @RequestParam("documentFileId") Long documentFileId, LithiumTokenUtil tokenUtil) throws Status404DocumentNotFoundException {
        return Response.<DocumentFile>builder().data(documentService.getDocumentFile(documentFileId)).build();
    }

    @PostMapping( value = {"/regular/update", "/sensitive/update" })
    public Response<Void> update(@PathVariable("domain") String domain, @RequestBody DocumentInfo documentInfo, LithiumTokenUtil tokenUtil) throws Status404DocumentNotFoundException {
        documentService.updateDocument(documentInfo, tokenUtil);
        return Response.<Void>builder().build();
    }

    @Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
    @PostMapping(value = {"/regular/upload", "/sensitive/upload"})
    public Response<DocumentInfo> uploadNew(@PathVariable("domain") String domain,
                                            @RequestPart("file") final MultipartFile multipartFile,
                                            @RequestPart("ownerGuid") String ownerGuid,
                                            @RequestParam("documentTypeId") Long documentTypeId,
                                            @RequestPart("reviewStatusName") String reviewStatusName,
                                            @RequestParam(name = "reviewReasonId", required = false) Long reviewReasonId,
                                            @RequestParam(name = "sensitive", required = false) boolean sensitive,
                                            LithiumTokenUtil tokenUtil) throws Exception {

        DocumentInfo documentInfo = documentService.uploadAndCreateDocument(multipartFile, ownerGuid, documentTypeId, reviewStatusName, reviewReasonId, sensitive, tokenUtil);
        return Response.<DocumentInfo>builder().data(documentInfo).build();
    }

    @DeleteMapping("/delete")
    public Response<Void> delete(@PathVariable("domain") String domain, @RequestParam Long id, LithiumTokenUtil tokenUtil) throws Status404DocumentNotFoundException {
        documentService.deleteDocument(id, tokenUtil);
        return Response.<Void>builder().build();
    }

    @GetMapping("/available-types")
    public Response<List<TextValue>> availableTypes(@PathVariable("domain") String domain, @RequestParam(value = "internalOnly", required = false) boolean internalOnly, LithiumTokenUtil tokenUtil) {
        return Response.<List<TextValue>>builder().data(documentTypeService.enabledTypesPerDomain(domain, internalOnly)).build();
    }

    @GetMapping("/available-review-reasons")
    public Response<List<TextValue>> availableReviewReasons(@PathVariable("domain") String domain, LithiumTokenUtil tokenUtil) {
        return Response.<List<TextValue>>builder().data(documentService.availableReviewReasons(domain)).build();
    }

}
