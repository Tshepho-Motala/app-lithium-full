package lithium.service.casino.provider.slotapi.services;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.metrics.TimeThisMethod;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.slotapi.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status474BetRoundNotFoundException;
import lithium.service.casino.provider.slotapi.api.schema.betresult.BetResultRequest;
import lithium.service.casino.provider.slotapi.api.schema.betresult.BetResultResponse;
import lithium.service.casino.provider.slotapi.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.slotapi.context.BetResultContext;
import lithium.service.casino.provider.slotapi.services.betresult.BetResultPhase1Validate;
import lithium.service.casino.provider.slotapi.services.betresult.BetResultPhase2Persist;
import lithium.service.casino.provider.slotapi.services.betresult.BetResultPhase3CallCasino;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
public class BetResultService {

    @Autowired @Setter
    BetResultPhase1Validate phase1Validate;

    @Autowired @Setter
    BetResultPhase2Persist phase2Persist;

    @Autowired @Setter
    BetResultPhase3CallCasino phase3CallCasino;


    @Retryable(exclude = { NotRetryableErrorCodeException.class })
    @TimeThisMethod
    @Transactional(rollbackOn = Exception.class)
    public BetResultResponse betResult(
        BetResultRequest request
    ) throws
        Status422DataValidationError,
        Status470HashInvalidException,
        Status474BetRoundNotFoundException,
        Status500UnhandledCasinoClientException,
        Status500ProviderNotConfiguredException
    {
        BetResultContext context = new BetResultContext();
        BetResultResponse response = new BetResultResponse();
        context.setRequest(request);
        context.setResponse(response);

        try {
            phase1Validate.validate(context);
            try {
                phase2Persist.persist(context, request);
                phase3CallCasino.callCasino(context, request);
            } catch (Status409DuplicateSubmissionException e) {
                log.warn(e.getMessage());
            }

            response.setLithiumBetResultId(context.getBetResult().getLithiumAccountingId());
            response.setBalance(context.getBetResult().getBalanceAfter());

            log.info("betresult " + context);

        } catch (ErrorCodeException ece) {
            log.warn("betresult error " + ExceptionMessageUtil.allMessages(ece) + " " + context);
            throw ece;
        } catch (Exception e) {
            log.error("betresult error " + ExceptionMessageUtil.allMessages(e) + " " + context, e);
            throw e;
        }


        return response;
    }
}
