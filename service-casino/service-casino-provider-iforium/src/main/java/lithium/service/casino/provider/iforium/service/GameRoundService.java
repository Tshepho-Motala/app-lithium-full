package lithium.service.casino.provider.iforium.service;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.casino.provider.iforium.model.request.AwardWinningsRequest;
import lithium.service.casino.provider.iforium.model.request.CreditRequest;
import lithium.service.casino.provider.iforium.model.request.EndRequest;
import lithium.service.casino.provider.iforium.model.request.PlaceBetRequest;
import lithium.service.casino.provider.iforium.model.request.RollBackBetRequest;
import lithium.service.casino.provider.iforium.model.request.VoidBetRequest;
import lithium.service.casino.provider.iforium.model.response.BalanceResponse;
import lithium.service.casino.provider.iforium.model.response.GameRoundResponse;
import lithium.service.casino.provider.iforium.model.response.PlaceBetResponse;
import lithium.service.client.LithiumServiceClientFactoryException;

public interface GameRoundService {

    PlaceBetResponse placeBet(PlaceBetRequest placeBetRequest, String domainName
    ) throws
            LithiumServiceClientFactoryException,
            NotRetryableErrorCodeException,
            Status511UpstreamServiceUnavailableException;

    BalanceResponse end(EndRequest endRequest, String domainName
    ) throws
            LithiumServiceClientFactoryException,
            NotRetryableErrorCodeException,
            Status500UnhandledCasinoClientException, Status511UpstreamServiceUnavailableException;

    GameRoundResponse awardWinnings(AwardWinningsRequest awardWinningsRequest, String domainName
    ) throws
            LithiumServiceClientFactoryException,
            NotRetryableErrorCodeException,
            Status500UnhandledCasinoClientException,
            Status511UpstreamServiceUnavailableException;

    GameRoundResponse rollBackBet(RollBackBetRequest rollBackBetRequest, String domainName
    ) throws
            LithiumServiceClientFactoryException,
            NotRetryableErrorCodeException,
            Status511UpstreamServiceUnavailableException;

    GameRoundResponse voidBet(VoidBetRequest voidBetRequest, String domainName
    ) throws
            LithiumServiceClientFactoryException,
            NotRetryableErrorCodeException,
            Status511UpstreamServiceUnavailableException;

    GameRoundResponse credit(CreditRequest creditRequest, String domainName) throws ErrorCodeException;
}
