package lithium.service.cashier.processor.paystack.api.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipient {
    private Boolean active;
    private String currency;
    private String description;
    private String domain;
    private String email;
    private String id;
    private String integration;
    private Metadata metadata;
    private String name;
    @JsonProperty("recipient_code")
    private String recipientCode;
    private String type;
    @JsonProperty("is_deleted")
    private Boolean isDeleted;
    private RecipientDetails details;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
}
