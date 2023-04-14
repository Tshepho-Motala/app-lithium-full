package lithium.service.kyc.provider.paystack.data.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BvnResolveResponse {
    private String status;
    private String message;
    private BvnResolveData data;
    private BvnResolveMeta meta;
}
