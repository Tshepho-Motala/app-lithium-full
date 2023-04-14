package lithium.service.casino.provider.sportsbook.context;

import lithium.service.casino.provider.sportsbook.api.schema.settledebit.SettleDebitRequest;
import lithium.service.casino.provider.sportsbook.api.schema.settledebit.SettleDebitResponse;
import lithium.service.casino.provider.sportsbook.storage.entities.Bet;
import lithium.service.casino.provider.sportsbook.storage.entities.Currency;
import lithium.service.casino.provider.sportsbook.storage.entities.Domain;
import lithium.service.casino.provider.sportsbook.storage.entities.SettlementCredit;
import lithium.service.casino.provider.sportsbook.storage.entities.SettlementDebit;
import lithium.service.casino.provider.sportsbook.storage.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettleDebitContext {
    String locale;
    User user;
    Domain domain;
    Currency currency;
    SettlementDebit settlementDebit;
    SettleDebitRequest request;
    SettleDebitResponse response;
    String convertedGuid;
}
