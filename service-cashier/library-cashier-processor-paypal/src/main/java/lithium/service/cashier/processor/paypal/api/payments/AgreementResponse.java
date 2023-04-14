package lithium.service.cashier.processor.paypal.api.payments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.paypal.api.Link;
import lithium.service.cashier.processor.paypal.api.orders.Payments;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class AgreementResponse {
    private String id;
    private String state;
    private String description;
    private Merchant merchant;
    private Payer payer;
    private Plan plan;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("update_time")
    private String updateTime;
    @JsonProperty("canceled_time")
    private String canceledTime;
    private List<Link> links;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Merchant {
        @JsonProperty("payee_info")
        private PayeeInfo payeeInfo;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class PayeeInfo {
        private String email;
    }
}
