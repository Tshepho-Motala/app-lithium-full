package lithium.service.casino.provider.slotapi.api.controllers.external;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.casino.SystemBonusClientService;
import lithium.service.casino.client.data.BonusAllocatev2;
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
import lithium.service.casino.provider.slotapi.api.exceptions.Status404NoSuchUserException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.slotapi.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.slotapi.api.schema.bonus.BonusRequest;
import lithium.service.casino.provider.slotapi.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.slotapi.services.SecurityService;
import lithium.service.casino.provider.slotapi.services.bonus.BonusValidateService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
public class ExternalBonusController {

    @Autowired
    SystemBonusClientService systemBonusClientService;

    @Autowired @Setter
    BonusValidateService bonusValidateService;

    @Autowired SecurityService securityService;

    @PostMapping("/bonus/trigger")
    public Response<Long> bonusTrigger(
                @RequestBody BonusRequest bonusRequest,
                @RequestHeader("Authorization") String authorization)
            throws
                Status422DataValidationError,
                Status444UniqueConstraintViolationException,
                Status470HashInvalidException,
                Status500InternalServerErrorException {

        BonusAllocatev2 bonusAllocatev2 = null;
        try {

            bonusValidateService.validateBonusTrigger(bonusRequest,bonusRequest.getPlayerGuid(), authorization);

            bonusAllocatev2 = BonusAllocatev2.builder()
                    .requestId(bonusRequest.getRequestId())
                    .customAmountDecimal(bonusRequest.getCustomAmountDecimal())
                    .bonusCode(bonusRequest.getBonusCode())
                    .description(bonusRequest.getDescription())
                    .playerGuid(bonusRequest.getPlayerGuid())
                    .clientId(bonusValidateService.getClientId(authorization))
                    .build();

            return systemBonusClientService.registerForCashBonus(bonusAllocatev2);
        } catch (Status401UnAuthorisedException |
                Status404NoSuchUserException |
                Status411InvalidUserGuidException |
                Status412InvalidCustomFreeMoneyAmountException |
                Status413NoValidBonusFoundForCodeException |
                Status414NoValidBonusRevisionFoundException |
                Status415BonusPrerequisitesNotMetException |
                Status416BonusUptakeLimitExceededException |
                Status417BonusIsNotValidForPlayerException |
                Status418FailedToAllocateFreeMoneyException |
                Status419FailedToAllocateExternalBonusException |
                Status420BonusCompleteCheckException |
                Status422InvalidParameterProvidedException |
                Status425FailedToAllocateBonusTokenException |
                Status422DataValidationError validationError
        ) {
            log.error("Problem registering the bonus: status -> {}, message -> {}", validationError.getErrorCode(), validationError.getMessage());
            throw new Status422DataValidationError(validationError.getMessage());
        } catch (Status444UniqueConstraintViolationException constraintViolationException) {
            log.error("Problem registering the bonus: status -> {}, message -> {}", constraintViolationException.getErrorCode(), constraintViolationException.getLocalizedMessage());
            throw constraintViolationException;
        } catch (Status470HashInvalidException hashInvalidException) {
            log.error("Problem registering the bonus: status -> {}, message -> {}", hashInvalidException.getErrorCode(), hashInvalidException.getMessage());
            throw hashInvalidException;
        } catch (Exception ex) {
            log.error("Problem registering the bonus: " + bonusAllocatev2, ex);
            throw new Status500InternalServerErrorException(ex.getMessage());
        }
    }

    @GetMapping("/bonus/{bonusType}/find/active")
    public List<String> findActiveBonuses(@PathVariable("bonusType") String bonusType,
                                          @RequestParam("domainName") String domainName,
                                          @RequestParam("sha256") String sha256,
                                          @RequestHeader("Authorization") String authorization)
            throws Status500InternalServerErrorException,
            Status470HashInvalidException,
            Status401UnAuthorisedException {
        try {
            securityService.validateSha256(domainName, new String[] {domainName}, sha256);
            securityService.validateBasicAuth(authorization);
            return systemBonusClientService.findActiveBonuses(bonusType, domainName);
        } catch (Status500UnhandledCasinoClientException | Status500ProviderNotConfiguredException e) {
            log.error("Problem retrieving active bonuses for bonusType -> {}, domainName -> {}", bonusType, domainName);
            throw new Status500InternalServerErrorException("Internal Server Error: error message: " + e.getMessage());
        }
    }
}
