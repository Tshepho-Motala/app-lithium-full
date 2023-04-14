
package lithium.service.cashier.processor.paypal.api.payouts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SenderBatchHeader {

    @JsonProperty("email_message")
    private String emailMessage;

    @JsonProperty("email_subject")
    private String emailSubject;

    @JsonProperty("recipient_type")
    private String recipientType;

    @JsonProperty("sender_batch_id")
    private String senderBatchId;
}
