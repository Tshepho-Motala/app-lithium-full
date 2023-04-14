package lithium.service.casino.provider.slotapi.services.bonus;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status404NoSuchUserException;
import lithium.service.casino.provider.slotapi.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.slotapi.api.exceptions.Status470HashInvalidException;
import lithium.service.casino.provider.slotapi.api.schema.bonus.BonusRequest;
import lithium.service.casino.provider.slotapi.config.Status500ProviderNotConfiguredException;
import lithium.service.casino.provider.slotapi.services.SecurityService;
import lithium.service.casino.provider.slotapi.services.oauthClient.OauthApiInternalClientService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BonusValidateService {

    @Autowired UserApiInternalClientService userApiInternalClientService;
    @Autowired OauthApiInternalClientService oauthApiInternalClientService;
    @Autowired SecurityService securityService;

    public void validateBonusTrigger(BonusRequest request,  String playerGuid,  String authorization)
            throws
                Status401UnAuthorisedException,
                Status404NoSuchUserException,
                Status422DataValidationError,
                Status500ProviderNotConfiguredException,
                Status470HashInvalidException {

        if (request.getRequestId() == null) {
            log.error("Problem registering the bonus: request id must be specified");
            throw new Status422DataValidationError("The request ID must be specified");
        }

        securityService.validateSha256(playerGuid.split("/")[0], new String[]{String.valueOf(request.getRequestId()), String.valueOf(request.getCustomAmountDecimal())}, request.getSha256());
        securityService.validateBasicAuth(authorization);

        try {
            userApiInternalClientService.getUserByGuid(playerGuid);
        } catch (UserClientServiceFactoryException | Exception e) {
            log.error("Problem registering the bonus: invalid playerGuid used to identify user" + playerGuid, e.getMessage());
            throw new Status404NoSuchUserException();
        }
    }

    public String getClientId(String authorization) {
        return oauthApiInternalClientService.getClientId(authorization);
    }
}
