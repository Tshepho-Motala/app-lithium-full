package lithium.service.casino.provider.iforium.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum  FundsPriorities {

    UNKNOWN("Unknown"),
    BONUS_FUNDS_FIRST("BonusFundsFirst"),
    CASH_FUNDS_FIRST("CashFundsFirst");

    final String name;
}
