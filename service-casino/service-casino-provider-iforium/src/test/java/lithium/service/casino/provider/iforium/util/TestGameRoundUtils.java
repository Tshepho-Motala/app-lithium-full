package lithium.service.casino.provider.iforium.util;

import lithium.service.casino.provider.iforium.constant.TestConstants;
import lithium.service.casino.provider.iforium.model.request.AwardWinningsRequest;
import lithium.service.casino.provider.iforium.model.request.CreditRequest;
import lithium.service.casino.provider.iforium.model.request.EndRequest;
import lithium.service.casino.provider.iforium.model.request.GameRoundRequest;
import lithium.service.casino.provider.iforium.model.request.PlaceBetRequest;
import lithium.service.casino.provider.iforium.model.request.RollBackBetRequest;
import lithium.service.casino.provider.iforium.model.request.VoidBetRequest;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Date;

@UtilityClass
public final class TestGameRoundUtils {

    public static PlaceBetRequest validPlaceBetRequest(BigDecimal amount) {
        PlaceBetRequest placeBetRequest = new PlaceBetRequest();
        fillValidParametersForGameRoundRequest(placeBetRequest);

        placeBetRequest.setGameRoundTransactionId(TestConstants.GAME_ROUND_TRANSACTION_ID);
        placeBetRequest.setAmount(amount);
        placeBetRequest.setStartRound(true);
        placeBetRequest.setEndRound(false);

        return placeBetRequest;
    }

    public static EndRequest validEndRequest() {
        EndRequest endRequest = new EndRequest();
        endRequest.setPlatformKey(TestConstants.PLATFORM_KEY);
        endRequest.setSequence(TestConstants.SEQUENCE);
        endRequest.setTimestamp(new Date());
        endRequest.setGatewaySessionToken(TestConstants.GATEWAY_SESSION_TOKEN);
        endRequest.setOperatorAccountId(TestConstants.OPERATOR_ACCOUNT_ID);
        endRequest.setGameRoundId(TestConstants.GAME_ROUND_ID);
        endRequest.setGameId(TestConstants.GAME_ID);
        endRequest.setCurrencyCode(TestConstants.GBP_CURRENCY);
        endRequest.setContentGameProviderId(TestConstants.CONTENT_GAME_PROVIDER_ID);
        endRequest.setFreeGameOfferCode(TestConstants.FREE_GAME_OFFER_CODE);
        return endRequest;
    }

    public static AwardWinningsRequest validAwardWinningsRequest(BigDecimal amount) {
        AwardWinningsRequest awardWinningsRequest = new AwardWinningsRequest();
        fillValidParametersForGameRoundRequest(awardWinningsRequest);

        awardWinningsRequest.setGameRoundTransactionId(TestConstants.GAME_ROUND_TRANSACTION_ID);
        awardWinningsRequest.setAmount(amount);
        awardWinningsRequest.setStartRound(true);
        awardWinningsRequest.setEndRound(false);
        awardWinningsRequest.setJackpotWinnings(BigDecimal.ZERO);

        return awardWinningsRequest;
    }

    public static RollBackBetRequest validRollBackBetRequest(BigDecimal amount) {
        RollBackBetRequest rollBackBetRequest = new RollBackBetRequest();
        fillValidParametersForGameRoundRequest(rollBackBetRequest);

        rollBackBetRequest.setOriginalBetGameRoundTransactionId(TestConstants.ORIGINAL_BET_GAME_ROUND_TRANSACTION_ID);
        rollBackBetRequest.setGameRoundTransactionId(TestConstants.GAME_ROUND_TRANSACTION_ID);
        rollBackBetRequest.setAmount(amount);
        rollBackBetRequest.setEndRound(false);

        return rollBackBetRequest;
    }

    public static VoidBetRequest validVoidBetRequest(boolean optional, BigDecimal amount) {
        VoidBetRequest voidBetRequest = new VoidBetRequest();
        fillValidParametersForGameRoundRequest(voidBetRequest);

        voidBetRequest.setGameRoundTransactionId(TestConstants.GAME_ROUND_TRANSACTION_ID);
        voidBetRequest.setCurrencyCode(TestConstants.GBP_CURRENCY);
        voidBetRequest.setAmount(amount);
        voidBetRequest.setEndRound(false);

        if (optional) {
            voidBetRequest.setGameVersion(TestConstants.GAME_VERSION);
            voidBetRequest.setTableId(TestConstants.TABLE_ID);
            voidBetRequest.setContentGameProviderId(TestConstants.CONTENT_GAME_PROVIDER_ID);
            voidBetRequest.setJackpotContribution(TestConstants.JACKPOT_CONTRIBUTION);
            voidBetRequest.setFreeGameOfferCode(TestConstants.FREE_GAME_OFFER_CODE);
        }

        return voidBetRequest;
    }

    public static CreditRequest validCreditRequest(BigDecimal amount) {
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setPlatformKey(TestConstants.PLATFORM_KEY);
        creditRequest.setSequence(TestConstants.SEQUENCE);
        creditRequest.setTimestamp(new Date());
        creditRequest.setOperatorAccountId(TestConstants.OPERATOR_ACCOUNT_ID);
        creditRequest.setAccountTransactionId(TestConstants.ACCOUNT_TRANSACTION_ID);
        creditRequest.setAccountTransactionTypeId(TestConstants.SHADOW_WITHDRAWAL_TRANSACTION_TYPE_ID);
        creditRequest.setCurrencyCode(TestConstants.GBP_CURRENCY);
        creditRequest.setAmount(amount);

        return creditRequest;
    }

    private static void fillValidParametersForGameRoundRequest(GameRoundRequest gameRoundRequest) {
        gameRoundRequest.setPlatformKey(TestConstants.PLATFORM_KEY);
        gameRoundRequest.setSequence(TestConstants.SEQUENCE);
        gameRoundRequest.setTimestamp(new Date());
        gameRoundRequest.setGatewaySessionToken(TestConstants.GATEWAY_SESSION_TOKEN);
        gameRoundRequest.setOperatorAccountId(TestConstants.OPERATOR_ACCOUNT_ID);
        gameRoundRequest.setGameRoundId(TestConstants.GAME_ROUND_ID);
        gameRoundRequest.setGameId(TestConstants.GAME_ID);
        gameRoundRequest.setCurrencyCode(TestConstants.GBP_CURRENCY);
    }

    public static AwardWinningsRequest validJackpotAwardWinningsRequest(BigDecimal amount) {
        AwardWinningsRequest awardWinningsRequest = new AwardWinningsRequest();
        fillValidParametersForGameRoundRequest(awardWinningsRequest);

        awardWinningsRequest.setGameRoundTransactionId(TestConstants.GAME_ROUND_TRANSACTION_ID);
        awardWinningsRequest.setAmount(amount);
        awardWinningsRequest.setStartRound(true);
        awardWinningsRequest.setEndRound(false);
        awardWinningsRequest.setJackpotWinnings(amount);

        return awardWinningsRequest;
    }
}
