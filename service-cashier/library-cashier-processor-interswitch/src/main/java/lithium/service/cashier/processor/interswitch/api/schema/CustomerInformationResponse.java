package lithium.service.cashier.processor.interswitch.api.schema;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;
import java.util.List;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "CustomerInformationResponse")
public class CustomerInformationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @JacksonXmlProperty(localName = "MerchantReference")
    private String merchantReference;	 // Mandatory	ANY	Alphanumeric

    @JacksonXmlElementWrapper(localName = "Customers")
    @JacksonXmlProperty(localName = "Customer")
    private List<Customer> customerList;
}





