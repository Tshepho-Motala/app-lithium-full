package lithium.service.cashier.processor.interswitch.api.schema;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName="PaymentNotificationResponse")
public class PaymentNotificationResponse {
    @JacksonXmlElementWrapper(localName="Payments")
    @JacksonXmlProperty(localName = "Payment")
    List<NotificationPayment> notificationPaymentsList;
}

