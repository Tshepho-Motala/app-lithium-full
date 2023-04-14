package lithium.service.cashier.processor.opay.api.v2.schema.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder(alphabetic=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToWalletRequest {
    private String amount;
    private String country;
    private String currency;
    private String reason;
    private Reciever receiver;
    private String reference;

    @Data
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonPropertyOrder(alphabetic=true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Reciever {
        private String name;
        private String phoneNumber;
        private String type;
    }
}
