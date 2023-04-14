package lithium.service.kyc.provider.onfido.controller;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.onfido.exceptions.Status400DisabledOnfidoReportException;
import lithium.service.kyc.provider.onfido.exceptions.Status411FailOnfidoServiceException;
import lithium.service.kyc.provider.onfido.exceptions.Status412NotFoundApplicantException;
import lithium.service.kyc.provider.onfido.exceptions.Status415NoDocumentToCheckException;
import lithium.service.kyc.provider.onfido.objects.ApplicantDto;
import lithium.service.kyc.provider.onfido.objects.StatusResponse;
import lithium.service.kyc.provider.onfido.service.OnfidoApplicantService;
import lithium.service.kyc.provider.onfido.service.OnfidoService;
import lithium.tokens.LithiumTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/frontend/api")
@Slf4j
public class FrontendApiController {
    private final OnfidoService onfidoService;
    private final OnfidoApplicantService applicantService;

    @GetMapping("/{domainName}/get-applicant")
    public ApplicantDto getApplicantId(@PathVariable String domainName, LithiumTokenUtil tokenUtil) throws Status512ProviderNotConfiguredException, Status500InternalServerErrorException, Status411FailOnfidoServiceException {
        log.info("Got request for retrieve applicant (" + tokenUtil.guid() + ")");
        return applicantService.getApplicantDto(tokenUtil);
    }

    @PostMapping("/{domainName}/submit-check")
    public StatusResponse submitCheck(@PathVariable String domainName,
                                      @RequestParam(name = "reportType", required = false, defaultValue = "document_with_address_information") String reportType,
                                      @RequestParam(name = "documentIds", required = false) String[] documentIds,
                                      LithiumTokenUtil tokenUtil) throws Status400DisabledOnfidoReportException, Status411FailOnfidoServiceException, Status512ProviderNotConfiguredException, Status500InternalServerErrorException, Status412NotFoundApplicantException, Status415NoDocumentToCheckException {
        log.info("Got request for submit check (" + tokenUtil.guid() + ") of " + reportType);
        return onfidoService.submitCheck(tokenUtil.guid(), tokenUtil.domainName(), reportType, documentIds);
    }
    
    @GetMapping("/{domainName}/status-check")
    public StatusResponse statusCheck(@PathVariable String domainName, LithiumTokenUtil tokenUtil) throws Status412NotFoundApplicantException {
        log.info("Got request for status check (" + tokenUtil.guid() + ")");
        return onfidoService.onfidoStatusCheck(tokenUtil.guid());
    }
}
