package lithium.service.cashier.processor.interswitch.api.schema;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @JacksonXmlProperty(localName = "Status")
    private Integer status; // Mandatory;	This is a status returned indicating if the customer is valid or not.
                                       //                0 = Valid
                                       //                1 = Invalid
                                       //                2 = Expired

    @JacksonXmlProperty(localName ="StatusMessage")  // Optional	Description of the Status
    private String statusMessage;

    @JacksonXmlProperty(localName ="CustReference")  // Mandatory	Unique ID for the paying customer
    private String custReference;

    @JacksonXmlProperty(localName ="CustomerReferenceAlternate")  // Optional	This is an alternate ID used to identify the same Customer
    private String customerReferenceAlternate;

    @JacksonXmlProperty(localName ="FirstName")  //Mandatory	This is the first name of customer
    private String firstName;

    @JacksonXmlProperty(localName ="LastName")  //Mandatory	This is the surname of customer
    private String lastName;

    @JacksonXmlProperty(localName ="OtherName")  //Mandatory	This is the middle name of customer
    private String otherName;

    @JacksonXmlProperty(localName ="Email")      // Optional	This is the email address of the customer
    private String email;

    @JacksonXmlProperty(localName ="Phone")      // Optional	This is the phone number of the customer
    private String phone;

    @JacksonXmlProperty(localName ="ThirdPartyCode")  // Optional	This is a unique code agreed between both Systems to group the customer in a category.
    private String thirdPartyCode;

    @JacksonXmlProperty(localName ="Amount")
    private BigDecimal amount;

    @JacksonXmlElementWrapper(localName="PaymentItems")
    @JacksonXmlProperty(localName ="Item")
    private List<PaymentItem> paymentItems;
}
