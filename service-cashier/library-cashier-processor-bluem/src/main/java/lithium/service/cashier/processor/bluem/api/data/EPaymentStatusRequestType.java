
package lithium.service.cashier.processor.bluem.api.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for EPaymentStatusRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EPaymentStatusRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionID" type="{}TransactionIDType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="entranceCode" use="required" type="{}EntranceCodeSimpleType" />
 *       &lt;attribute name="brandID" type="{}RelaxedIdentifierSimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EPaymentStatusRequestType", propOrder = {
    "transactionID"
})
public class EPaymentStatusRequestType {

    @XmlElement(name = "TransactionID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String transactionID;
    @XmlAttribute(name = "entranceCode", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String entranceCode;
    @XmlAttribute(name = "brandID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String brandID;

    /**
     * Gets the value of the transactionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionID() {
        return transactionID;
    }

    /**
     * Sets the value of the transactionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionID(String value) {
        this.transactionID = value;
    }

    /**
     * Gets the value of the entranceCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntranceCode() {
        return entranceCode;
    }

    /**
     * Sets the value of the entranceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntranceCode(String value) {
        this.entranceCode = value;
    }

    /**
     * Gets the value of the brandID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrandID() {
        return brandID;
    }

    /**
     * Sets the value of the brandID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrandID(String value) {
        this.brandID = value;
    }

}
