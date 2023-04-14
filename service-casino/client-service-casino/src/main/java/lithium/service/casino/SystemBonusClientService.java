package lithium.service.casino;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.casino.client.SystemBonusClient;
import lithium.service.casino.client.data.BonusAllocatev2;
import lithium.service.casino.client.data.PlayerBonusHistory;
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
import lithium.service.casino.exceptions.Status422InvalidParameterProvidedException;
import lithium.service.casino.exceptions.Status425FailedToAllocateBonusTokenException;
import lithium.service.casino.exceptions.Status444UniqueConstraintViolationException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class SystemBonusClientService implements SystemBonusClient {

    @Autowired @Setter
    LithiumServiceClientFactory services;

    private SystemBonusClient getSystemBonusClient() throws Status500UnhandledCasinoClientException {
        try {
            return services.target(SystemBonusClient.class,"service-casino", true);
        } catch (Exception e) {
            log.error("Problem getting system bonus client", e);
            throw new Status500UnhandledCasinoClientException("Unable to retrieve system bonus client proxy: " + e.getMessage());
        }
    }

    @Override
    public PlayerBonusHistory findPlayerBonusHistoryById(Long playerBonusHistoryId)
    throws Exception
    {
        return getSystemBonusClient().findPlayerBonusHistoryById(playerBonusHistoryId);
    }

    @Override
    public Response<Long> registerForCashBonus(BonusAllocatev2 bonusAllocatev2)
            throws Status411InvalidUserGuidException,
                Status412InvalidCustomFreeMoneyAmountException,
                Status413NoValidBonusFoundForCodeException,
                Status422InvalidParameterProvidedException,
                Status444UniqueConstraintViolationException,
                Status500InternalServerErrorException,
                Status500UnhandledCasinoClientException,
                Status415BonusPrerequisitesNotMetException,
                Status425FailedToAllocateBonusTokenException,
                Status420BonusCompleteCheckException,
                Status414NoValidBonusRevisionFoundException,
                Status416BonusUptakeLimitExceededException,
                Status419FailedToAllocateExternalBonusException,
                Status417BonusIsNotValidForPlayerException,
                Status418FailedToAllocateFreeMoneyException {

        return getSystemBonusClient().registerForCashBonus(bonusAllocatev2);
    }

    @Override
    public Response<Long> massActionRegisterForCashBonus(BonusAllocatev2 bonusAllocatev2) throws Status411InvalidUserGuidException, Status413NoValidBonusFoundForCodeException, Status422InvalidParameterProvidedException, Status415BonusPrerequisitesNotMetException, Status414NoValidBonusRevisionFoundException, Status412InvalidCustomFreeMoneyAmountException, Status425FailedToAllocateBonusTokenException, Status416BonusUptakeLimitExceededException, Status418FailedToAllocateFreeMoneyException, Status420BonusCompleteCheckException, Status417BonusIsNotValidForPlayerException, Status419FailedToAllocateExternalBonusException, Status500UnhandledCasinoClientException {
        return getSystemBonusClient().massActionRegisterForCashBonus(bonusAllocatev2);
    }

    @Override
    public List<String> findActiveBonuses(String bonusType, String domainName) throws Status500UnhandledCasinoClientException {
        return getSystemBonusClient().findActiveBonuses(bonusType, domainName);
    }
}