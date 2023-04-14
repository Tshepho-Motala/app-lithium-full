package lithium.service.cashier.processor.interswitch.api.schema;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentItem implements Serializable {

    private static final long serialVersionUID = 1L;

    // Mandatory if returning payment item

    @JacksonXmlProperty(localName ="ProductName")
    private String productName;

    @JacksonXmlProperty(localName ="ProductCode")	// This is the code of the item that the customer should/can pay for
    private String productCode;

    @JacksonXmlProperty(localName ="Quantity")	// Numeric	This is the number of item that customer should/can pay for
    private Long quantity;

    @JacksonXmlProperty(localName ="Price")	// This is the unit price of the item that the customer should/can pay for
    private BigDecimal price;

    @JacksonXmlProperty(localName ="Subtotal")	// This is the total amount that the customer should/can pay for LESS tax
    private BigDecimal subtotal;

    @JacksonXmlProperty(localName ="Tax")	// This is the additional cost added to the original cost of the item
    private BigDecimal tax;

    @JacksonXmlProperty(localName ="Total")	// This is the total amount that the customer should/can pay for.
    private BigDecimal total;
}
