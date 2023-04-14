package lithium.service.casino.client;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
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
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

/**
 * Allows for system-to-system integration of bonus functions
 */
@FeignClient(name="service-casino")
public interface SystemBonusClient {

    @RequestMapping(path = "/system/casino/bonus/player/history/{playerBonusHistoryId}", method= RequestMethod.GET)
    PlayerBonusHistory findPlayerBonusHistoryById(@PathVariable("playerBonusHistoryId") Long playerBonusHistoryId)
    throws Exception;


    /**
     * Triggers a cash bonus to be created on a player
     */
    @RequestMapping(path = "/system/casino/bonus/trigger", method= RequestMethod.POST)
    @ResponseBody
    public Response<Long> registerForCashBonus(@RequestBody BonusAllocatev2 bonusAllocatev2)
            throws
                    Status411InvalidUserGuidException,
                    Status412InvalidCustomFreeMoneyAmountException,
                    Status413NoValidBonusFoundForCodeException,
                    Status414NoValidBonusRevisionFoundException,
                    Status415BonusPrerequisitesNotMetException,
                    Status416BonusUptakeLimitExceededException,
                    Status417BonusIsNotValidForPlayerException,
                    Status418FailedToAllocateFreeMoneyException,
                    Status419FailedToAllocateExternalBonusException,
                    Status420BonusCompleteCheckException,
                    Status422InvalidParameterProvidedException,
                    Status425FailedToAllocateBonusTokenException,
                    Status444UniqueConstraintViolationException,
                    Status500InternalServerErrorException,
                    Status500UnhandledCasinoClientException;

    @RequestMapping(path = "/system/casino/bonus/mass-action-trigger", method = RequestMethod.POST)
    public Response<Long> massActionRegisterForCashBonus(@RequestBody BonusAllocatev2 bonusAllocatev2)
            throws
            Status411InvalidUserGuidException,
            Status413NoValidBonusFoundForCodeException,
            Status422InvalidParameterProvidedException,
            Status415BonusPrerequisitesNotMetException,
            Status414NoValidBonusRevisionFoundException,
            Status412InvalidCustomFreeMoneyAmountException,
            Status425FailedToAllocateBonusTokenException,
            Status416BonusUptakeLimitExceededException,
            Status418FailedToAllocateFreeMoneyException,
            Status420BonusCompleteCheckException,
            Status417BonusIsNotValidForPlayerException,
            Status419FailedToAllocateExternalBonusException, Status500UnhandledCasinoClientException;

    @RequestMapping(path = "/system/casino/bonus/{bonusType}/find/active", method = RequestMethod.GET)
    public List<String> findActiveBonuses(@PathVariable("bonusType") String bonusType, @RequestParam("domainName") String domainName) throws Status500UnhandledCasinoClientException;
}
