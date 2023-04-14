package lithium.service.casino.provider.incentive.services;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.metrics.TimeThisMethod;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.incentive.api.exceptions.Status404BetTransactionNotFoundException;
import lithium.service.casino.provider.incentive.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.incentive.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.incentive.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.incentive.api.schema.settlement.SettlementRequest;
import lithium.service.casino.provider.incentive.api.schema.settlement.SettlementResponse;
import lithium.service.casino.provider.incentive.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.incentive.context.SettlementContext;
import lithium.service.casino.provider.incentive.services.settlement.SettlementPhase1Validate;
import lithium.service.casino.provider.incentive.services.settlement.SettlementPhase2Persist;
import lithium.service.casino.provider.incentive.services.settlement.SettlementPhase3CallCasino;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
public class SettlementService {

    @Autowired @Setter
    SettlementPhase1Validate phase1Validate;

    @Autowired @Setter
    SettlementPhase2Persist phase2Persist;

    @Autowired @Setter
    SettlementPhase3CallCasino phase3CallCasino;

    @Retryable(exclude = { NotRetryableErrorCodeException.class })
    @TimeThisMethod
    @Transactional(rollbackOn = Exception.class)
    public SettlementResponse settle(
        SettlementRequest request
    ) throws
        Status404BetTransactionNotFoundException,
        Status409DuplicateSubmissionException,
        Status422DataValidationError,
        Status470HashInvalidException,
        Status500ProviderNotConfiguredException,
        Status500UnhandledCasinoClientException
    {
        SettlementContext context = new SettlementContext();
        phase1Validate.validate(context, request);
        phase2Persist.persist(context, request);
        phase3CallCasino.callCasino(context, request);

        SettlementResponse response = new SettlementResponse();
        response.setLithiumSettlementId(context.getSettlement().getId());
        response.setPlayerBalance(context.getBalance());
        return response;
    }
}
