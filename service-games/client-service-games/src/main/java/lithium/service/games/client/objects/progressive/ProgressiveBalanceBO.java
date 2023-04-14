package lithium.service.games.client.objects.progressive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgressiveBalanceBO {

    private String gameName;

    private String gameGuid;

    private String providerUrl;

    private String progressiveId;

    private String gameSupplierName;

    private String currency;

    private BigDecimal amount;


}
