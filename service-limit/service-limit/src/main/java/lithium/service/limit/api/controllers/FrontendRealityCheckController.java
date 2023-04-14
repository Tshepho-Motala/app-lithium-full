package lithium.service.limit.api.controllers;


import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.limit.data.entities.RealityCheckSet;
import lithium.service.limit.enums.RealityCheckStatusType;
import lithium.service.limit.services.RealityCheckService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frontend/reality-check/v1")
@Slf4j
public class FrontendRealityCheckController {
    @Autowired  RealityCheckService userRealityCheckService;
    @Autowired  MessageSource messageSource;

    @GetMapping("/get")
    public RealityCheckSet get(LithiumTokenUtil token) throws UserNotFoundException, Status500InternalServerErrorException {
        try {
            return userRealityCheckService.getOrCreateCurrentRealitySet(token.guid());
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.LOGIN.INTERNAL_SERVER_ERROR", new Object[]{new lithium.service.translate.client.objects.Domain(token.guid().split("/")[0])}, "Internal server error.", LocaleContextHolder.getLocale()), e.getStackTrace());
        } catch (Status500InternalServerErrorException internalServerErrorException) {
            throw new Status500InternalServerErrorException(messageSource.getMessage("ERROR_DICTIONARY.LOGIN.INTERNAL_SERVER_ERROR", new Object[]{new lithium.service.translate.client.objects.Domain(token.guid().split("/")[0])}, "Internal server error.", LocaleContextHolder.getLocale()), internalServerErrorException.getStackTrace());
        }
    }

    @PostMapping("/set")
    public RealityCheckSet set(LithiumTokenUtil token, @RequestParam long realityCheckTime) throws UserNotFoundException, Status500InternalServerErrorException {
        return userRealityCheckService.setRealityCheckTimerTime(token.guid(), token.guid(), realityCheckTime, null);
    }

    @PostMapping("/track")
    public ResponseEntity track(LithiumTokenUtil token, @RequestParam RealityCheckStatusType action) {
        try {
            userRealityCheckService.logUserChoice(token.guid(), action);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (UserClientServiceFactoryException | Exception e) {
            log.error("Failed to track reality check period  [guid=" + token.guid() + "] " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
