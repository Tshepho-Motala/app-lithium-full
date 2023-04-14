package lithium.service.translate.controllers.external;

import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status412DomainNotFoundException;
import lithium.exceptions.Status413EcosystemNotFoundException;
import lithium.exceptions.Status414LocaleNotFoundOrDisabledException;
import lithium.exceptions.Status470HashInvalidException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.translate.data.objects.ExternalErrorDictionary;
import lithium.service.translate.services.ExternalTranslationsService;
import lithium.service.translate.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.List;

@RestController
@EnableCustomHttpErrorCodeExceptions
@RequestMapping("/external/translations/")
public class ExternalTranslationsController {

    @Autowired
    ExternalTranslationsService externalTranslationsService;

    @Autowired
    SecurityService securityService;

    @GetMapping("/error-dictionary")
    public List<ExternalErrorDictionary> getAllErrorDictionaryMessages(
            @RequestParam(name = "domainName", required = false) String domainName,
            @RequestParam(name = "ecosystemName", required = false) String ecosystemName,
            @RequestParam(name = "locale", required = false) String locale,
            @RequestParam(name = "lastUpdatedSince", defaultValue = "2000-01-01 00:00:00") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date lastUpdatedSince,
            @RequestParam(name = "hash") String hash,
            @RequestParam(name = "apiAuthorizationId", defaultValue = "ls-gw") String apiAuthorizationId,
            @RequestHeader("Authorization") String authorization)
            throws Status413EcosystemNotFoundException, Status412DomainNotFoundException,
            Status470HashInvalidException, Status414LocaleNotFoundOrDisabledException,
            Status550ServiceDomainClientException, Status401UnAuthorisedException {
        securityService.validateBasicAuth(authorization);
        return externalTranslationsService.getAllErrorDictionaryMessages(domainName, ecosystemName, locale, lastUpdatedSince, hash, apiAuthorizationId);
    }
}
