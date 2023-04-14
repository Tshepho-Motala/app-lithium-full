package lithium.service.casino.api.system;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.casino.client.data.BonusAllocatev2;
import lithium.service.casino.client.data.PlayerBonusHistory;
import lithium.service.casino.client.data.SourceSystem;
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
import lithium.service.casino.service.BonusService;
import lithium.service.casino.service.CasinoBonusService;
import lithium.service.casino.service.SystemBonusTriggerService;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status416PlayerPromotionsBlockedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/system")
public class SystemBonusController {

    @Autowired SystemBonusTriggerService systemBonusTriggerService;
    @Autowired BonusService bonusService;
    @Autowired
    CasinoBonusService casinoBonusService;
    @Autowired LimitInternalSystemService limitInternalSystemService;
    @Autowired LocaleContextProcessor localeContextProcessor;

    @PostMapping("/casino/bonus/trigger")
    public Response<Long> registerForCashBonus(
            @RequestBody BonusAllocatev2 bonusAllocatev2,
            @RequestParam(value = "locale", required = false) String locale)
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
            Status416PlayerPromotionsBlockedException {
        try {
            String[] domainAndPlayer = bonusAllocatev2.getPlayerGuid().split("/");
            localeContextProcessor.setLocaleContextHolder(locale, domainAndPlayer[0]);

            log.info("System bonus trigger (post) request (" + bonusAllocatev2.getPlayerGuid() + ") :: " + bonusAllocatev2);

            limitInternalSystemService.checkPromotionsAllowed(bonusAllocatev2.getPlayerGuid());

            bonusAllocatev2.setSourceSystem(SourceSystem.SERVICE_CASINO_PROVIDER_SLOTAPI);
            systemBonusTriggerService.systemBonusTrigger(bonusAllocatev2);

            return Response.<Long>builder().status(Response.Status.OK).build();
        } catch (Status411InvalidUserGuidException |
                Status412InvalidCustomFreeMoneyAmountException |
                Status413NoValidBonusFoundForCodeException |
                Status422InvalidParameterProvidedException |
                Status444UniqueConstraintViolationException |
                Status415BonusPrerequisitesNotMetException |
                Status417BonusIsNotValidForPlayerException |
                Status425FailedToAllocateBonusTokenException |
                Status414NoValidBonusRevisionFoundException |
                Status416BonusUptakeLimitExceededException |
                Status419FailedToAllocateExternalBonusException |
                Status420BonusCompleteCheckException |
                Status418FailedToAllocateFreeMoneyException |
                Status416PlayerPromotionsBlockedException errorCodeException
        ) {
            log.error("Problem performing a system bonus trigger request: status -> {}, message -> {}", errorCodeException.getErrorCode(), errorCodeException.getMessage());
            throw errorCodeException;
        } catch (Exception ex) {
            log.error("Internal system error while registering for a bonus trigger request: " + bonusAllocatev2, ex);
            throw new Status500InternalServerErrorException(ex.getMessage());
        }
    }

    @TimeThisMethod
    @PostMapping("/casino/bonus/mass-action-trigger")
    public Response<Long> massActionRegisterForCashBonus(
            @RequestBody BonusAllocatev2 bonusAllocatev2,
            @RequestParam(value = "locale", required = false) String locale)
            throws Status413NoValidBonusFoundForCodeException,
            Status412InvalidCustomFreeMoneyAmountException,
            Status411InvalidUserGuidException, Status422InvalidParameterProvidedException,
            Status416BonusUptakeLimitExceededException,
            Status419FailedToAllocateExternalBonusException,
            Status414NoValidBonusRevisionFoundException,
            Status415BonusPrerequisitesNotMetException,
            Status422InvalidGrantBonusException,
            Status418FailedToAllocateFreeMoneyException,
            Status417BonusIsNotValidForPlayerException,
            Status425FailedToAllocateBonusTokenException,
            Status420BonusCompleteCheckException,
            Status416PlayerPromotionsBlockedException {
        String[] domainAndPlayer = bonusAllocatev2.getPlayerGuid().split("/");
        localeContextProcessor.setLocaleContextHolder(locale, domainAndPlayer[0]);
        log.info("System bonus trigger (post) request (" + bonusAllocatev2.getPlayerGuid() + ") :: " + bonusAllocatev2);

        SW.start("checkPromotionsAllowed");
        limitInternalSystemService.checkPromotionsAllowed(bonusAllocatev2.getPlayerGuid());
        SW.stop();

        bonusAllocatev2.setSourceSystem(SourceSystem.SERVICE_USER_MASS_ACTION);

        SW.start("systemMassActionBonusTrigger");
        systemBonusTriggerService.systemMassActionBonusTrigger(bonusAllocatev2);
        SW.stop();

        return Response.<Long>builder().status(Response.Status.OK).build();
    }

    @GetMapping("/casino/bonus/{bonusType}/find/active")
    public List<String> findActiveBonuses(@RequestParam("domainName") String domainName) {

        return bonusService.getActiveCashBonuses(domainName).stream().map(bonus -> bonus.getBonusCode()).collect(Collectors.toList());
    }

    @GetMapping("/casino/bonus/player/history/{playerBonusHistoryId}")
    public PlayerBonusHistory findPlayerBonusHistory(@PathVariable("playerBonusHistoryId") Long playerBonusHistoryId) {

        return casinoBonusService.findPlayerBonusHistoryById(playerBonusHistoryId);
    }
}
