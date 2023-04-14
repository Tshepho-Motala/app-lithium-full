package lithium.service.cashier.processor.paystack.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class TransferRecipientResponseData {
    private Boolean active;
    private String createdAt;
    private String currency;
    private String description;
    private String domain;
    private String email;
    private Integer id;
    private Integer integration;
    private Metadata metadata;
    private String name;
    @JsonProperty("recipient_code")
    private String recipientCode;
    private String type;
    private String updatedAt;
    @JsonProperty("is_deleted")
    private Boolean isDeleted;
    private AccountDetails details;
}


