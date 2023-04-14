package lithium.service.casino.provider.iforium.util;

import lithium.math.CurrencyAmount;
import lithium.service.casino.provider.iforium.constant.Constants;
import lithium.service.casino.provider.iforium.model.response.Balance;
import lithium.service.casino.provider.iforium.model.response.BalanceResponse;
import lithium.service.casino.provider.iforium.model.response.FundsPriorities;
import lithium.service.domain.client.objects.Domain;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static lithium.service.casino.provider.iforium.model.response.ErrorCodes.SUCCESS;

@UtilityClass
public final class BalanceUtils {

    private static final int DECIMAL_POINT = 2;
    private static final int JACKPOT_CONTRIBUTION_DECIMAL_POINT = 8;

    public static Balance buildBalance(String currencyCode, BigDecimal cashFunds) {
        return Balance.builder()
                      .currencyCode(currencyCode)
                      .cashFunds(cashFunds)
                      .bonusFunds(Constants.ZERO_AMOUNT)
                      .fundsPriority(FundsPriorities.UNKNOWN.getName())
                      .build();
    }

    public static long convertToCurrencyCent(BigDecimal amount) {
        return amount.movePointRight(DECIMAL_POINT).longValueExact();
    }

    public static long convertJackpotContributionToCurrencyCent(BigDecimal amount) {
        amount = amount.setScale(JACKPOT_CONTRIBUTION_DECIMAL_POINT, RoundingMode.HALF_UP);
        return amount.movePointRight(JACKPOT_CONTRIBUTION_DECIMAL_POINT).longValue();
    }

    public static BigDecimal convertToCurrencyUnit(long cents) {
        return CurrencyAmount.fromCents(cents).toAmount();
    }

    public static BalanceResponse buildBalanceResponse(Domain domain, BigDecimal playerBalance) {
        Balance balance = BalanceUtils.buildBalance(domain.getCurrency(), playerBalance);
        return BalanceResponse.builder().errorCode(SUCCESS.getCode()).balance(balance).build();
    }
}
