package lithium.service.access.provider.kycgbg.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GbgResponseData {
    private String bandText;
    private Integer scorePoints;
    private boolean dobMatched;
    private boolean addressMatched;
    private boolean lastNameMatched;
    private String providerRequestId;
}
