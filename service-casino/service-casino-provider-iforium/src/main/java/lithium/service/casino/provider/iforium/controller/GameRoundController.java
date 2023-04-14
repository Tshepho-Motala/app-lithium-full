package lithium.service.casino.provider.iforium.controller;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.provider.iforium.constant.Constants;
import lithium.service.casino.provider.iforium.model.request.AwardWinningsRequest;
import lithium.service.casino.provider.iforium.model.request.CreditRequest;
import lithium.service.casino.provider.iforium.model.request.EndRequest;
import lithium.service.casino.provider.iforium.model.request.PlaceBetRequest;
import lithium.service.casino.provider.iforium.model.request.RollBackBetRequest;
import lithium.service.casino.provider.iforium.model.request.VoidBetRequest;
import lithium.service.casino.provider.iforium.model.response.GameRoundResponse;
import lithium.service.casino.provider.iforium.model.response.BalanceResponse;
import lithium.service.casino.provider.iforium.model.response.PlaceBetResponse;
import lithium.service.casino.provider.iforium.model.validation.GatewaySessionTokenNotBlankValidation;
import lithium.service.casino.provider.iforium.model.validation.GatewaySessionTokenNotNullValidation;
import lithium.service.casino.provider.iforium.service.GameRoundService;
import lithium.service.casino.provider.iforium.util.RequestUtils;
import lithium.service.casino.provider.iforium.util.SecurityConfigUtils;
import lithium.service.casino.provider.iforium.util.WebUtils;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GameRoundController {

    private final GameRoundService service;
    private final SecurityConfigUtils securityConfigUtils;

    @PostMapping("${api.iforium.game-round.place-bet.path}")
    public PlaceBetResponse placeBet(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization,
                                     @RequestHeader(value = Constants.X_FORWARDED_FOR) String xForwardedFor,
                                     @Validated({GatewaySessionTokenNotBlankValidation.class, GatewaySessionTokenNotNullValidation.class})
                                     @RequestBody PlaceBetRequest placeBetRequest
    ) throws
            NotRetryableErrorCodeException,
            LithiumServiceClientFactoryException,
            Status511UpstreamServiceUnavailableException
    {
        String domainName = RequestUtils.getDomainNameFromPlayerGuid(placeBetRequest.getOperatorAccountId());
        securityConfigUtils.validateSecurity(authorization, WebUtils.getFirstIpFromXForwardedFor(xForwardedFor), domainName);
        log.info("Place bet request {}", placeBetRequest);
        PlaceBetResponse placeBetResponse = service.placeBet(placeBetRequest, domainName);
        log.info("Place bet response {}",placeBetResponse);
        return placeBetResponse;
    }

    @PostMapping("${api.iforium.game-round.end.path}")
    public BalanceResponse end(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization,
                               @RequestHeader(value = Constants.X_FORWARDED_FOR) String xForwardedFor,
                               @Valid @RequestBody EndRequest endRequest
    ) throws
            NotRetryableErrorCodeException,
            LithiumServiceClientFactoryException,
            Status500UnhandledCasinoClientException,
            Status511UpstreamServiceUnavailableException {
        log.info("GameRound end request {}", endRequest);
        String domainName = RequestUtils.getDomainNameFromPlayerGuid(endRequest.getOperatorAccountId());
        securityConfigUtils.validateSecurity(authorization, WebUtils.getFirstIpFromXForwardedFor(xForwardedFor), domainName);
        BalanceResponse balanceResponse = service.end(endRequest, domainName);
        log.info("GameRound end response {}", balanceResponse);
        return balanceResponse;

    }

    @PostMapping("${api.iforium.game-round.award-winnings.path}")
    public GameRoundResponse awardWinnings(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization,
                                           @RequestHeader(value = Constants.X_FORWARDED_FOR) String xForwardedFor,
                                           @Valid @RequestBody AwardWinningsRequest awardWinningsRequest
    ) throws
            NotRetryableErrorCodeException,
            LithiumServiceClientFactoryException,
            Status500UnhandledCasinoClientException,
            Status511UpstreamServiceUnavailableException
    {
        log.info("GameRound award winnings request {}", awardWinningsRequest);
        String domainName = RequestUtils.getDomainNameFromPlayerGuid(awardWinningsRequest.getOperatorAccountId());
        securityConfigUtils.validateSecurity(authorization, WebUtils.getFirstIpFromXForwardedFor(xForwardedFor), domainName);
        GameRoundResponse gameRoundResponse = service.awardWinnings(awardWinningsRequest, domainName);
        log.info("GameRound award winnings response {}", gameRoundResponse);
        return gameRoundResponse;
    }

    @PostMapping("${api.iforium.game-round.roll-back-bet.path}")
    public GameRoundResponse rollBackBet(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization,
                                         @RequestHeader(value = Constants.X_FORWARDED_FOR) String xForwardedFor,
                                         @Valid @RequestBody RollBackBetRequest rollBackBetRequest
    ) throws NotRetryableErrorCodeException,
            LithiumServiceClientFactoryException,
            Status511UpstreamServiceUnavailableException
    {
        log.info("GameRound roll back bet request {}", rollBackBetRequest);
        String domainName = RequestUtils.getDomainNameFromPlayerGuid(rollBackBetRequest.getOperatorAccountId());
        securityConfigUtils.validateSecurity(authorization, WebUtils.getFirstIpFromXForwardedFor(xForwardedFor), domainName);
        GameRoundResponse gameRoundResponse = service.rollBackBet(rollBackBetRequest, domainName);
        log.info("GameRound roll back bet response {}", gameRoundResponse);
        return gameRoundResponse;

    }

    @PostMapping("${api.iforium.game-round.void-bet.path}")
    public GameRoundResponse voidBet(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization,
                                     @RequestHeader(value = Constants.X_FORWARDED_FOR) String xForwardedFor,
                                     @Valid @RequestBody VoidBetRequest voidBetRequest) throws NotRetryableErrorCodeException, LithiumServiceClientFactoryException, Status511UpstreamServiceUnavailableException {
        log.info("GameRound void bet request {}", voidBetRequest);
        String domainName = RequestUtils.getDomainNameFromPlayerGuid(voidBetRequest.getOperatorAccountId());
        securityConfigUtils.validateSecurity(authorization, WebUtils.getFirstIpFromXForwardedFor(xForwardedFor), domainName);
        GameRoundResponse gameRoundResponse = service.voidBet(voidBetRequest, domainName);
        log.info("GameRound void bet response {}", gameRoundResponse);
        return gameRoundResponse;
    }

    @PostMapping("${api.iforium.account-transaction.credit.path}")
    public GameRoundResponse credit(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization,
                                    @RequestHeader(value = Constants.X_FORWARDED_FOR) String xForwardedFor,
                                    @Valid @RequestBody CreditRequest creditRequest) throws ErrorCodeException {
        log.info(" Iforium Account  transaction credit  request {}", creditRequest);
        String domainName = RequestUtils.getDomainNameFromPlayerGuid(creditRequest.getOperatorAccountId());
        securityConfigUtils.validateSecurity(authorization, WebUtils.getFirstIpFromXForwardedFor(xForwardedFor), domainName);
        GameRoundResponse gameRoundResponse = service.credit(creditRequest, domainName);
        log.info(" Iforium Account  transaction credit  response {}", gameRoundResponse);
        return gameRoundResponse;
    }
}
