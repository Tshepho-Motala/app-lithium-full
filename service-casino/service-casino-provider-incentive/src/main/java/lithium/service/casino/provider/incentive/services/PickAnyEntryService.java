package lithium.service.casino.provider.incentive.services;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.metrics.TimeThisMethod;
import lithium.service.casino.provider.incentive.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.incentive.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.incentive.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.incentive.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.incentive.context.PickAnyEntryContext;
import lithium.service.casino.provider.incentive.services.pickanyentry.PickAnyEntryPhase1Validate;
import lithium.service.casino.provider.incentive.services.pickanyentry.PickAnyEntryPhase2Persist;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PickAnyEntryService {

    @Autowired @Setter
    PickAnyEntryPhase1Validate phase1Validate;

    @Autowired @Setter
    PickAnyEntryPhase2Persist phase2Persist;

    @Retryable(exclude = { NotRetryableErrorCodeException.class })
    @TimeThisMethod
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void pickAnyEntry(PickAnyEntryContext context) throws Status401UnAuthorisedException,
            Status405UserDisabledException, Status500UserInternalSystemClientException,
            Status490SoftSelfExclusionException, Status500LimitInternalSystemClientException,
            Status491PermanentSelfExclusionException, Status470HashInvalidException,
            Status422DataValidationError, Status500ProviderNotConfiguredException,
            Status409DuplicateSubmissionException, Status496PlayerCoolingOffException {
        phase1Validate.validate(context);
        phase2Persist.persist(context);
    }

}
