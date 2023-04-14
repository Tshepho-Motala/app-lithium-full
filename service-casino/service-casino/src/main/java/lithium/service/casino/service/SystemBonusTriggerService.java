package lithium.service.casino.service;

import lithium.service.casino.client.data.BonusAllocatev2;
import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.repositories.PlayerBonusHistoryRepository;
import lithium.service.casino.exceptions.Status411InvalidUserGuidException;
import lithium.service.casino.exceptions.Status412InvalidCustomFreeMoneyAmountException;
import lithium.service.casino.exceptions.Status413NoValidBonusFoundForCodeException;
import lithium.service.casino.exceptions.Status414NoValidBonusRevisionFoundException;
import lithium.service.casino.exceptions.Status415BonusPrerequisitesNotMetException;
import lithium.service.casino.exceptions.Status416BonusUptakeLimitExceededException;
import lithium.service.casino.exceptions.Status417BonusIsNotValidForPlayerException;
import lithium.service.casino.exceptions.Status418FailedToAllocateFreeMoneyException;
import lithium.service.casino.exceptions.Status419FailedToAllocateExternalBonusException;
import lithium.service.casino.exceptions.Status420BonusCompleteCheckException;
import lithium.service.casino.exceptions.Status422InvalidGrantBonusException;
import lithium.service.casino.exceptions.Status422InvalidParameterProvidedException;
import lithium.service.casino.exceptions.Status425FailedToAllocateBonusTokenException;
import lithium.service.casino.exceptions.Status444UniqueConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class SystemBonusTriggerService {

    @Autowired
    CasinoTriggerBonusService casinoTriggerBonusService;

    @Autowired
    PlayerBonusHistoryRepository playerBonusHistoryRepository;

    /**
     * Used to register a system bonus trigger
     *
     * @param bonusAllocatev2
     * @throws Status411InvalidUserGuidException
     * @throws Status412InvalidCustomFreeMoneyAmountException
     * @throws Status413NoValidBonusFoundForCodeException
     * @throws Status422InvalidParameterProvidedException
     * @throws Status444UniqueConstraintViolationException
     * @throws Status415BonusPrerequisitesNotMetException
     * @throws Status417BonusIsNotValidForPlayerException
     * @throws Status425FailedToAllocateBonusTokenException
     * @throws Status414NoValidBonusRevisionFoundException
     * @throws Status416BonusUptakeLimitExceededException
     * @throws Status419FailedToAllocateExternalBonusException
     * @throws Status420BonusCompleteCheckException
     * @throws Status418FailedToAllocateFreeMoneyException
     */
    public void systemBonusTrigger(
            BonusAllocatev2 bonusAllocatev2
    ) throws
            Status411InvalidUserGuidException,
            Status412InvalidCustomFreeMoneyAmountException,
            Status413NoValidBonusFoundForCodeException,
            Status422InvalidParameterProvidedException,
            Status444UniqueConstraintViolationException,
            Status415BonusPrerequisitesNotMetException,
            Status417BonusIsNotValidForPlayerException,
            Status425FailedToAllocateBonusTokenException,
            Status414NoValidBonusRevisionFoundException,
            Status416BonusUptakeLimitExceededException,
            Status419FailedToAllocateExternalBonusException,
            Status420BonusCompleteCheckException,
            Status418FailedToAllocateFreeMoneyException, Status422InvalidGrantBonusException {

        Optional<PlayerBonusHistory> byRequestIdAndClientId = playerBonusHistoryRepository.findByRequestIdAndClientId(bonusAllocatev2.getRequestId(), bonusAllocatev2.getClientId());
        if (byRequestIdAndClientId.isPresent()) {
            throw new Status444UniqueConstraintViolationException("(request_id & client_id is not unique)");
        }

        casinoTriggerBonusService.processTriggerOrTokenBonusWithCustomMoney(bonusAllocatev2, BonusRevision.BONUS_TYPE_TRIGGER, null);
    }

    /**
     * Used by service-user-mass-action to create mass cash bonuses for players via a csv file
     */
    public void systemMassActionBonusTrigger(BonusAllocatev2 bonusAllocatev2) throws Status411InvalidUserGuidException, Status413NoValidBonusFoundForCodeException, Status417BonusIsNotValidForPlayerException, Status415BonusPrerequisitesNotMetException, Status414NoValidBonusRevisionFoundException, Status412InvalidCustomFreeMoneyAmountException, Status425FailedToAllocateBonusTokenException, Status416BonusUptakeLimitExceededException, Status418FailedToAllocateFreeMoneyException, Status420BonusCompleteCheckException, Status419FailedToAllocateExternalBonusException, Status422InvalidParameterProvidedException, Status422InvalidGrantBonusException {
        casinoTriggerBonusService.processTriggerOrTokenBonusWithCustomMoney(bonusAllocatev2, BonusRevision.BONUS_TYPE_TRIGGER, null);
    }
}
