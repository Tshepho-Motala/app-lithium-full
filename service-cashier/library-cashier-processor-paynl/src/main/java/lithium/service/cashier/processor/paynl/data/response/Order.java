package lithium.service.cashier.processor.paynl.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String id;
    private String countryCode;
    private String deliveryDate;
    private String invoiceDate;
    private DeliveryAddress deliveryAddress;
    private InvoiceAddress invoiceAddress;
    private List<String> products;
}
