package lithium.service.access.provider.sphonic.cruks.controllers.backoffice;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.access.provider.sphonic.cruks.services.CRUKSService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lithium.service.Response;

import static lithium.service.Response.Status.OK_SUCCESS;

@RestController
@RequestMapping("/backoffice/validation")
public class BackofficeValidationController {

    @Autowired
    CRUKSService cruksService;

    @GetMapping("/{domainName}/cruks")
    public Response<String> store(@RequestParam("cruksId") String cruksId, @PathVariable("domainName") String domainName) throws Status550ServiceDomainClientException, Status512ProviderNotConfiguredException, Status500InternalServerErrorException {
        return Response.<String>builder().data(cruksService.validateCruksId(cruksId, domainName)).status(OK_SUCCESS).build();
    }
}
