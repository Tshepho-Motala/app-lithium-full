package lithium.service.casino.provider.incentive.api.controllers;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status444ReferencedEntityNotFound;
import lithium.service.casino.provider.incentive.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.incentive.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.incentive.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.incentive.api.schema.pickany.settlement.PickAnySettlementRequest;
import lithium.service.casino.provider.incentive.api.schema.pickany.settlement.PickAnySettlementResponse;
import lithium.service.casino.provider.incentive.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.incentive.context.PickAnySettlementContext;
import lithium.service.casino.provider.incentive.services.PickAnySettlementService;
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
public class PickAnySettlementController {

    @Autowired @Setter
    PickAnySettlementService service;

    @PostMapping("/pickany/settlement")
    public PickAnySettlementResponse pickAnyEntry(
            @RequestParam(defaultValue = "en_US") String locale,
            @RequestBody PickAnySettlementRequest request) throws
            Status422DataValidationError,
            Status444ReferencedEntityNotFound,
            Status470HashInvalidException,
            Status500ProviderNotConfiguredException {

        PickAnySettlementContext context = PickAnySettlementContext.builder()
                .request(request).response(new PickAnySettlementResponse()).locale(locale).build();

        try {
            try {
                service.pickAnySettlement(context);
                log.info("pickanysettlement success " + context);
            } catch (Status409DuplicateSubmissionException e) {
                log.warn("pickanysettlement duplicate request " + context);
            }
            context.getResponse().setLithiumEntryId(context.getSettlement().getEntry().getId());
            context.getResponse().setLithiumSettlementId(context.getSettlement().getId());
            return context.getResponse();
        } catch (ErrorCodeException ec) {
            log.warn("pickanysettlement error " + ec + " " + context);
            throw ec;
        } catch (Exception e) {
            log.error("pickanysettlement error " + ExceptionMessageUtil.allMessages(e) + " " + context, e);
            throw e;
        }

    }

}
