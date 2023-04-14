package lithium.service.casino.provider.iforium.context;

import lithium.service.games.client.objects.Game;
import lithium.service.reward.client.dto.PlayerRewardTypeHistory;
import lithium.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.BooleanUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Objects;

import static lithium.service.casino.provider.iforium.constant.Constants.ZERO_AMOUNT;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameRoundContext {
    private String domainName;

    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal jackpotWinnings = BigDecimal.ZERO;

    private String gameRoundId;
    private String gameRoundTransactionId;
    private boolean endRound;
    private String currencyCode;
    private String operatorAccountId;
    private String gatewaySessionToken;
    private Long loginEventId;
    private String freeGameOfferCode;
    private BigDecimal freeBetCost;

    private PlayerRewardTypeHistory playerRewardTypeHistory;

    private Game game;

    public String getGameGuid() {
        if (Objects.isNull(game)) {
            return null;
        }

        return game.getGuid();
    }

    public boolean hasFreeGame() {
        return Objects.nonNull(game) && BooleanUtils.isTrue(game.getFreeGame());
    }

    public boolean isFreePlay() {
        return !StringUtil.isEmpty(freeGameOfferCode) || (freeBetCost != null && freeBetCost.compareTo(BigDecimal.ZERO) > 0);
    }
    public boolean isWin() {
        boolean win;

        if (hasJackpotWinnings()) {
           win = true;
        } else {
            win = amount.compareTo(ZERO_AMOUNT) > 0;
        }

        return win;
    }

    public boolean hasJackpotWinnings() {
        return jackpotWinnings != null && jackpotWinnings.compareTo(ZERO_AMOUNT) > 0;
    }

    public Long getPlayerRewardTypeHistoryId() {
        return playerRewardTypeHistory != null ? playerRewardTypeHistory.getId(): null;
    }
}
