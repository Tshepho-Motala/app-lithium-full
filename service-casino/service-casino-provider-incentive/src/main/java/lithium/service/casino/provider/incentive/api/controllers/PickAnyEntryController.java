package lithium.service.casino.provider.incentive.api.controllers;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.service.casino.provider.incentive.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.incentive.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.incentive.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.incentive.api.schema.pickany.entry.PickAnyEntryRequest;
import lithium.service.casino.provider.incentive.api.schema.pickany.entry.PickAnyEntryResponse;
import lithium.service.casino.provider.incentive.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.incentive.context.PickAnyEntryContext;
import lithium.service.casino.provider.incentive.services.PickAnyEntryService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PickAnyEntryController {

    @Autowired @Setter
    PickAnyEntryService service;

    @PostMapping("/pickany/entry")
    public PickAnyEntryResponse pickAnyEntry(
                @RequestParam(defaultValue = "en_US") String locale,
                @RequestBody PickAnyEntryRequest request,
                LithiumTokenUtil token) throws
            Status401UnAuthorisedException,
            Status405UserDisabledException,
            Status422DataValidationError,
            Status470HashInvalidException,
            Status490SoftSelfExclusionException,
            Status491PermanentSelfExclusionException,
            Status500UserInternalSystemClientException,
            Status500LimitInternalSystemClientException,
            Status500ProviderNotConfiguredException,
            Status496PlayerCoolingOffException {

        PickAnyEntryContext context = PickAnyEntryContext.builder()
                .request(request).response(new PickAnyEntryResponse()).sessionId(token.sessionId())
                .domainName(token.domainName()).playerGuid(token.guid()).locale(locale).build();

        try {
            try {
                service.pickAnyEntry(context);
                log.info("pickanyentry success " + context);
            } catch (Status409DuplicateSubmissionException e) {
                log.warn("pickanyentry duplicate request " + context);
            }
            context.getResponse().setLithiumEntryId(context.getEntry().getId());
            return context.getResponse();
        } catch (ErrorCodeException ec) {
            log.warn("pickanyentry error " + ec + " " + context);
            throw ec;
        } catch (Exception e) {
            log.error("pickanyentry error " + ExceptionMessageUtil.allMessages(e) + " " + context, e);
            throw e;
        }
    }
}
