package lithium.service.casino.provider.incentive.services;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.exceptions.Status444ReferencedEntityNotFound;
import lithium.metrics.TimeThisMethod;
import lithium.service.casino.provider.incentive.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.incentive.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.incentive.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.incentive.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.incentive.context.PickAnySettlementContext;
import lithium.service.casino.provider.incentive.services.pickanysettlement.PickAnySettlementPhase1Validate;
import lithium.service.casino.provider.incentive.services.pickanysettlement.PickAnySettlementPhase2Persist;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PickAnySettlementService {

    @Autowired @Setter
    PickAnySettlementPhase1Validate phase1Validate;

    @Autowired @Setter
    PickAnySettlementPhase2Persist phase2Persist;

    @Retryable(exclude = { NotRetryableErrorCodeException.class })
    @TimeThisMethod
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void pickAnySettlement(PickAnySettlementContext context) throws Status470HashInvalidException,
            Status409DuplicateSubmissionException, Status444ReferencedEntityNotFound,
            Status500ProviderNotConfiguredException, Status422DataValidationError {
        phase1Validate.validate(context);
        phase2Persist.persist(context);
    }
}
