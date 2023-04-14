package lithium.service.access.controllers.external;

import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.exceptions.Status400BadRequestException;
import lithium.exceptions.Status404AccessRuleNotFoundException;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.access.controllers.external.schemas.ExternalValidationRequest;
import lithium.service.access.controllers.external.schemas.ExternalValidationResponse;
import lithium.service.access.services.ExternalValidationsService;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.translate.client.objects.RegistrationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@EnableCustomHttpErrorCodeExceptions
public class ExternalValidationsController {

  @Autowired ExternalValidationsService externalValidationsService;
  @Autowired MessageSource messageSource;

  @PostMapping("/external/authorization/{domainName}/{accessRuleName}/check-authorization")
  public Response<ExternalValidationResponse> externalValidate(@PathVariable("domainName") String domainName,
                                                                @PathVariable("accessRuleName") String accessRuleName,
                                                                @RequestBody ExternalValidationRequest externalValidationRequest,
                                                                @RequestHeader(value = "User-Agent") String userAgent,
                                                                @RequestParam(required = false, defaultValue = "false") Boolean test,
                                                                HttpServletRequest request)
      throws Status400BadRequestException, Status404AccessRuleNotFoundException, Status470HashInvalidException, Status474DomainProviderDisabledException, Status512ProviderNotConfiguredException, Status500InternalServerErrorException {
    try {
      return Response.<ExternalValidationResponse>builder()
          .data(externalValidationsService
              .doExternalValidations(externalValidationRequest, domainName, accessRuleName, request.getRemoteAddr(), userAgent, test))
          .status(Status.OK).build();
    } catch (Status400BadRequestException | Status404AccessRuleNotFoundException | Status470HashInvalidException | Status474DomainProviderDisabledException | Status512ProviderNotConfiguredException handledExceptions) {
      throw handledExceptions;
    } catch (Exception unhandledExceptions) {
      throw new Status500InternalServerErrorException(RegistrationError.INTERNAL_SERVER_ERROR.getResponseMessageLocal(messageSource, domainName), unhandledExceptions.getStackTrace());
    }

  }
}
