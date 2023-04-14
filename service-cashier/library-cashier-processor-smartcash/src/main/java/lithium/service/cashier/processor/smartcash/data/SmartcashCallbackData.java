package lithium.service.cashier.processor.smartcash.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lithium.service.cashier.processor.smartcash.SmartcashCallbackTransaction;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmartcashCallbackData {
    private SmartcashCallbackTransaction transaction;
    private String hash;
}
