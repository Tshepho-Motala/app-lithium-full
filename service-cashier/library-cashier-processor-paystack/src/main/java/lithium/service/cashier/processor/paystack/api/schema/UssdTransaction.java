package lithium.service.cashier.processor.paystack.api.schema;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UssdTransaction {
    private String status;
    private String reference;
    @JsonProperty("display_text")
    private String displayText;
    @JsonProperty("ussd_code")
    private String ussdCode;
    private String message;
}
