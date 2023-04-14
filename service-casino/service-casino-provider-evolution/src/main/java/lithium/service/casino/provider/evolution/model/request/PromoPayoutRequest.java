package lithium.service.casino.provider.evolution.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromoPayoutRequest extends Request {

    private String currency;

    private Game game;

    private Transaction transaction;

    private String uuid;

    private PromoTransaction promoTransaction;

}
