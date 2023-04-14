package lithium.service.casino.provider.evolution.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromoTransaction extends Transaction {

    private String id;

    private BigDecimal amount;

    private String type;

    private String voucherId;

    private Integer remainingRounds;

    private List<Jackpot> jackpots;

    private BigDecimal playableBalance;

    private String bonusConfigId;

    private String rewardId;

    private String uuid;

}
