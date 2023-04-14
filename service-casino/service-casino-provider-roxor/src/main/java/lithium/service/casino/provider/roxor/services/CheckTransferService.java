package lithium.service.casino.provider.roxor.services;

import lithium.modules.ModuleInfo;
import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status404NotFoundException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.api.schema.Money;
import lithium.service.casino.provider.roxor.api.schema.SuccessStatus;
import lithium.service.casino.provider.roxor.api.schema.Transfer;
import lithium.service.casino.provider.roxor.api.schema.checktransfer.CheckTransferRequest;
import lithium.service.casino.provider.roxor.api.schema.checktransfer.CheckTransferResponse;
import lithium.service.casino.provider.roxor.config.ProviderConfigService;
import lithium.service.casino.provider.roxor.context.GamePlayContext;
import lithium.service.casino.provider.roxor.storage.entities.GamePlay;
import lithium.service.casino.provider.roxor.storage.entities.Operation;
import lithium.service.casino.provider.roxor.storage.repositories.GamePlayRepository;
import lithium.service.casino.provider.roxor.storage.repositories.OperationRepository;
import lithium.service.casino.provider.roxor.util.ValidationHelper;
import lithium.service.user.client.objects.LoginEvent;
import lithium.util.ExceptionMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CheckTransferService {
    @Autowired ProviderConfigService providerConfigService;
    @Autowired ModuleInfo moduleInfo;
    @Autowired ValidationHelper validationHelper;
    @Autowired GamePlayRepository gamePlayRepository;
    @Autowired OperationRepository operationRepository;


    public CheckTransferResponse checkTransfer(
            String sessionKey,
            String xForwardFor,
            CheckTransferRequest checkTransferRequest
    ) throws
            Status400BadRequestException,
            Status401NotLoggedInException,
            Status404NotFoundException,
            Status500RuntimeException
    {
        try {
            log.info("checkTransfer request with sessionKey : " + sessionKey);

            //validate input
            if (checkTransferRequest == null) throw new Status400BadRequestException();
            if (checkTransferRequest.getGamePlayId() == null) throw new Status400BadRequestException();
            if (checkTransferRequest.getTransferId() == null) throw new Status400BadRequestException();

            String screenName = null;
            //validate SessionKey if populated
            if (sessionKey != null && !sessionKey.trim().isEmpty()) {
                LoginEvent lastLoginEvent = validationHelper.findLastLoginEventForSessionKey(new GamePlayContext(), sessionKey);
                if (lastLoginEvent != null) {
                    //validate user session matches authenticate request user

                    // FIXME: https://jira.livescore.com/browse/PLAT-1144
                    //        As far as I can see, we do not have player authentication at this point. We do not have player guid.
                    //        We only have a session key from which we pull the login event.
                    //        We're passing null for player guid which is validated against the login event user guid.
                    //        Thus always throwing Status401NotLoggedInException at lithium.service.casino.provider.roxor.util.ValidationHelper.validateUser.
                    //        User validation is not possible. There are however other validations done, f.eg website, so i'm letting this go through and
                    //        adding a check for null player guid on lithium.service.casino.provider.roxor.util.ValidationHelper.validateUser.

                    validationHelper.validate(
                            new GamePlayContext(),
                            lastLoginEvent,
                            null,
                            null,
                            xForwardFor
                    );
                } else {
                    throw new Status401NotLoggedInException();
                }
            }

            GamePlay gamePlay = gamePlayRepository.findByGuid(checkTransferRequest.getGamePlayId());
            if (gamePlay == null) {
                throw new Status404NotFoundException();
            }

            List<Operation> operationList = operationRepository.findByGamePlayAndTransferId(
                    gamePlay, checkTransferRequest.getTransferId());

            Operation resultOperation = operationList.stream()
                    .filter(o -> o.getStatus().name().equalsIgnoreCase(Operation.Status.RESULT.name()))
                    .findFirst()
                    .orElse(null);

            if (resultOperation == null) {
                throw new Status404NotFoundException();
            }

            return CheckTransferResponse.builder()
                    .status(SuccessStatus.builder()
                            .code(SuccessStatus.Code.OK)
                            .build()
                    )
                    .transfer(Transfer.builder()
                            .transferId(resultOperation.getTransferId())
                            .type(resultOperation.getType().getCode())
                            .amount(Money.builder()
                                    .amount(resultOperation.getAmountCents())
                                    .currency(resultOperation.getCurrency().getCode())
                                    .build()
                            ).build()
                    ).build();
        } catch (
                Status400BadRequestException |
                Status401NotLoggedInException |
                Status404NotFoundException e
        ) {
            log.warn("check-transfer [sessionKey="+sessionKey+", xForwardFor="+xForwardFor
                    +", checkTransferRequest="+checkTransferRequest+"] " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("check-transfer [sessionKey="+sessionKey+", xForwardFor="+xForwardFor
                    +", checkTransferRequest="+checkTransferRequest+"] " + ExceptionMessageUtil.allMessages(e), e);
            throw new Status500RuntimeException();
        }
    }
}
