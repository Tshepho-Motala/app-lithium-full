package lithium.service.cashier.processor.paystack.api.schema.deposit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.paystack.api.schema.AuthorizationData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackGetBinResponse {
    private boolean status;
    private String message;
    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class BinResponseData {
            private String bin;
            private String brand;
            private String sub_brand;
            private String country_code;
            private String country_name;
            private String card_type;
            private String bank;
            private Integer linked_bank_id;
    }
    private BinResponseData data;
}
