package lithium.service.cashier.client.frontend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AddProcessorAccountRequest {
    private String methodCode;
    private String redirectUrl;
    private Map<String, String> metadata;
}
