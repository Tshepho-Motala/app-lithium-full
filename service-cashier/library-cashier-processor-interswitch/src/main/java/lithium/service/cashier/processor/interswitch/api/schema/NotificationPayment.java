package lithium.service.cashier.processor.interswitch.api.schema;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NotificationPayment {
    @JacksonXmlProperty(localName = "PaymentLogId")
    private Long paymentLogId;	// Mandatory	ANY	Numeric	Unique integer ID for the payment
    @JacksonXmlProperty(localName = "Status")
    private Integer status;	// Mandatory	1	Numeric	Acknowledgement returned by Merchant to indicate if payment was received or not
                            //  0=Received / Duplicate Payment
                            //  1= Rejected by System
    @JacksonXmlProperty(localName = "StatusMessage")
    private String statusMessage;	// Optional	ANY	Alphanumeric	Description of the Status
}
