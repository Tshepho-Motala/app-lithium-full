package lithium.service.casino.provider.sportsbook.context;

import lithium.service.casino.provider.sportsbook.api.schema.settle.SettleMultiRequest;
import lithium.service.casino.provider.sportsbook.api.schema.settle.SettleMultiResponse;
import lithium.service.casino.provider.sportsbook.api.schema.settlecredit.SettleCreditRequest;
import lithium.service.casino.provider.sportsbook.api.schema.settlecredit.SettleCreditResponse;
import lithium.service.casino.provider.sportsbook.storage.entities.Bet;
import lithium.service.casino.provider.sportsbook.storage.entities.Currency;
import lithium.service.casino.provider.sportsbook.storage.entities.Domain;
import lithium.service.casino.provider.sportsbook.storage.entities.Settlement;
import lithium.service.casino.provider.sportsbook.storage.entities.SettlementCredit;
import lithium.service.casino.provider.sportsbook.storage.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettleMultiContext {
    private Locale locale;
    private User user;
    private Domain domain;
    private HashMap<String, Bet> betMap;
    private Currency currency;
    private Settlement settlement;
    private SettleMultiRequest request;
    private SettleMultiResponse response;
    private String convertedGuid;
}
