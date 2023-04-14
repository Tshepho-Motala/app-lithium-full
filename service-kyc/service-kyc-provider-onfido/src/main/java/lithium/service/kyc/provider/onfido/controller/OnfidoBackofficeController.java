package lithium.service.kyc.provider.onfido.controller;

import com.onfido.Onfido;
import com.onfido.exceptions.OnfidoException;
import com.onfido.models.Document;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.kyc.provider.onfido.objects.StatusResponse;
import lithium.service.kyc.provider.onfido.service.OnfidoApplicantService;
import lithium.service.kyc.provider.onfido.service.OnfidoDocumentService;
import lithium.service.kyc.provider.onfido.service.OnfidoService;
import lithium.tokens.LithiumTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/backoffice")
@Slf4j
public class OnfidoBackofficeController {
    private final OnfidoDocumentService documentService;
    private final OnfidoService onfidoService;
    private final OnfidoApplicantService applicantService;

    @RequestMapping(method = RequestMethod.POST, path = "/{domainName}/upload-document")
    public Response<String> uploadDocumentToExternalService(
            @PathVariable String domainName,
            @RequestPart("userGuid") String userGuid,
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "issuingCountry", required = false) String issuingCountry,
            @RequestPart(value = "type", required = false) String type,
            @RequestPart("side") String side,
            LithiumTokenUtil tokenUtil) {

        try {
            Onfido onfido = applicantService.getOnfidoClient(domainName);
            String applicantId = applicantService.updateAndGetApplicantId(userGuid, domainName, onfido);

            Document document = documentService.uploadDocument(onfido, applicantId, file, issuingCountry, type, side, tokenUtil.guid());
            return new Response<String>(document.getId());
        } catch (Status500InternalServerErrorException | IOException e) {
            log.error("Got internal error during upload document to Onfido(" + userGuid + "): " + e.getMessage(), e);
            return new Response<>(Response.Status.INTERNAL_SERVER_ERROR, "Internal error");
        } catch (OnfidoException e) {
            log.warn("Got error on Onfido side during upload document to Onfido(" + userGuid + "): " + e.getMessage(), e);
            return new Response<>(Response.Status.BAD_REQUEST, "Onfido service error");
        }
    }
    @RequestMapping(method = RequestMethod.POST, path = "/{domainName}/submit-check")
    public Response<String> submitCheck(
            @PathVariable String domainName,
            @RequestParam("userGuid") String userGuid,
            @RequestParam(name = "reportType", required = false, defaultValue = "document_with_address_information") String reportType ,
            LithiumTokenUtil tokenUtil) {
        try {
            StatusResponse response = onfidoService.submitCheck(userGuid, domainName, reportType, null);
            return new Response<String>(response.getStatus());
        } catch (Status500InternalServerErrorException e) {
            log.error("Got internal error during upload document to Onfido(" + userGuid + "): " + e.getMessage(), e);
            return new Response<>(Response.Status.INTERNAL_SERVER_ERROR, "Internal error");
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/{domainName}/status-check")
    public Response<String> statusCheck(@PathVariable String domainName,
                                        @RequestParam("userGuid") String userGuid,
                                        LithiumTokenUtil tokenUtil) {
        StatusResponse response = onfidoService.onfidoStatusCheck(userGuid);
        return new Response<String>(response.getStatus());
    }
}
