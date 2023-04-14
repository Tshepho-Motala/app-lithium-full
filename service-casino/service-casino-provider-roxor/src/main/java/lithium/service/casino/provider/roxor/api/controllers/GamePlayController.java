package lithium.service.casino.provider.roxor.api.controllers;

import lithium.service.Response;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status402InsufficientFundsException;
import lithium.service.casino.provider.roxor.api.exceptions.Status404NotFoundException;
import lithium.service.casino.provider.roxor.api.exceptions.Status440LossLimitException;
import lithium.service.casino.provider.roxor.api.exceptions.Status441TurnoverLimitException;
import lithium.service.casino.provider.roxor.api.exceptions.Status442LifetimeDepositException;
import lithium.service.casino.provider.roxor.api.exceptions.Status443TimeLimitException;
import lithium.service.casino.provider.roxor.api.exceptions.Status444DepositLimitException;
import lithium.service.casino.provider.roxor.api.exceptions.Status445GeoLocationException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.api.schema.Replay;
import lithium.service.casino.provider.roxor.api.schema.SuccessResponse;
import lithium.service.casino.provider.roxor.config.ProviderConfig;
import lithium.service.casino.provider.roxor.config.ProviderConfigService;
import lithium.service.casino.provider.roxor.services.GamePlayService;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RestController
@RequestMapping("/rgp")
public class GamePlayController {
    @Autowired @Setter
    GamePlayService gamePlayService;
    @Autowired @Setter
    ProviderConfigService providerConfigService;
    @Autowired @Setter
    CachingDomainClientService cachingDomainClientService;

    @Value("${lithium.services.casino.provider.roxor.replay-url.use-domain-as-website}")
    private boolean useDomainAsWebsite;

    @PostMapping("/game-play")
    public @ResponseBody
    SuccessResponse gamePlay(
            @RequestHeader("GameplayId") String gamePlayId,
            @RequestHeader(value = "SessionKey", required = false) String sessionKey,
            @RequestHeader(value = "X-Forward-For", required = false) String xForwardFor,
            @RequestBody String gamePlayRequestJsonString,
            @RequestParam(defaultValue = "en_US") String locale
    ) throws
            Status400BadRequestException,
            Status401NotLoggedInException,
            Status402InsufficientFundsException,
            Status404NotFoundException,
            Status440LossLimitException,
            Status441TurnoverLimitException,
            Status442LifetimeDepositException,
            Status443TimeLimitException,
            Status444DepositLimitException,
            Status445GeoLocationException,
            Status500RuntimeException {
        log.info("gamePlay request with SessionKey : " + sessionKey + " xForwardFor : " + xForwardFor +
                " gamePlayId : " + gamePlayId + " and Body : " + gamePlayRequestJsonString);

        SuccessResponse response = gamePlayService.gamePlay(
                gamePlayId,
                sessionKey,
                xForwardFor,
                gamePlayRequestJsonString,
                locale
        );

        log.info("gamePlay response with SessionKey : " + sessionKey + " xForwardFor : " + xForwardFor +
                " gamePlayId : " + gamePlayId + " and Body : " + response);

        return response;
    }

    @GetMapping("/game-replay")
    public Response<Replay> getReplayLink(
            @RequestParam("domainName") String domainName,
            @RequestParam("providerId") String providerId,
            @RequestParam("gameKey") String gameKey,
            @RequestParam("roundId") String roundId,
            @RequestParam("playerId") String playerId) {
        try {
            ProviderConfig providerConfig = null;
            Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
            if (domain != null) {
                try {
                    providerConfig = providerConfigService.getConfig(providerId, domainName);
                } catch (Exception ex) {
                    providerConfig = null;
                }
                if (providerConfig == null) {
                    domain = domain.getParent();
                    if (domain != null) {
                        providerConfig = providerConfigService.getConfig(providerId, domain.getName());
                    }
                }
            } else {
                throw new Status512ProviderNotConfiguredException(domainName);
            }
            if (providerConfig.getBetRoundReplayUrl() != null) {
                String countryCode = domain.getDefaultLocale().split("-").length > 0 ? domain.getDefaultLocale().split("-")[1] : domain.getDefaultLocale();
                String website = useDomainAsWebsite == true ? domainName : providerConfig.getWebsite();
                String replayUrl = providerConfig.getBetRoundReplayUrl().concat("?playMode=CASH").concat("&language=").concat(providerConfig.getLanguage()).concat("&currency=").concat(providerConfig.getCurrency()).concat("&country=").concat(countryCode).concat("&sessionKey=&clock=true&hideDepositButton=true&hideP4RButton=true&homePos=none&globalAlert=false&replay=1").concat("&website=").concat(website).concat("&gameKey=").concat(gameKey).concat("&playerId=").concat(playerId).concat("&gameplayId=").concat(roundId);
                return Response.<Replay>builder().data(Replay.builder().replayUrl(replayUrl).build()).status(Response.Status.OK).build();
            }
            throw new Status512ProviderNotConfiguredException(domainName);
        } catch (Exception ex) {
            return Response.<Replay>builder().message(ex.getMessage()).status(Response.Status.NOT_FOUND).build();
        }
    }
}
