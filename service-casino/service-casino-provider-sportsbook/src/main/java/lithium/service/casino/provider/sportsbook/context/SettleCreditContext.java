package lithium.service.casino.provider.sportsbook.context;

import lithium.service.casino.provider.sportsbook.api.schema.settlecredit.SettleCreditRequest;
import lithium.service.casino.provider.sportsbook.api.schema.settlecredit.SettleCreditResponse;
import lithium.service.casino.provider.sportsbook.storage.entities.Bet;
import lithium.service.casino.provider.sportsbook.storage.entities.Currency;
import lithium.service.casino.provider.sportsbook.storage.entities.Domain;
import lithium.service.casino.provider.sportsbook.storage.entities.SettlementCredit;
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
public class SettleCreditContext {
    String locale;
    User user;
    Domain domain;
    Bet bet;
    Currency currency;
    SettlementCredit settlementCredit;
    SettleCreditRequest request;
    SettleCreditResponse response;
    String convertedGuid;
}
