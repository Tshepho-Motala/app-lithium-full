package lithium.service.cashier.processor.hexopay.api.page.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Settings {
    @JsonProperty("return_url")
    private String returnUrl;
    @JsonProperty("success_url")
    private String successUrl;
    @JsonProperty("decline_url")
    private String declineUrl;
    @JsonProperty("fail_url")
    private String failUrl;
    @JsonProperty("cancel_url")
    private String cancelUrl;
    @JsonProperty("notification_url")
    private String notificationUrl;
    @JsonProperty("verification_url")
    private String verificationUrl;
    @JsonProperty("auto_return")
    private Integer autoReturn;
    @JsonProperty("button_text")
    private String buttonText;
    @JsonProperty("button_next_text")
    private String buttonNextText;
    private String language;
    //Object customer_fields;
}
