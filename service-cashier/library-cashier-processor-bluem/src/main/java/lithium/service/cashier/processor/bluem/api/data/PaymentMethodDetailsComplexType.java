
package lithium.service.cashier.processor.bluem.api.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentMethodDetailsComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentMethodDetailsComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IDealDetails" type="{}IDealDetailsComplexType" minOccurs="0"/>
 *         &lt;element name="PayPalDetails" type="{}PayPalDetailsComplexType" minOccurs="0"/>
 *         &lt;element name="VisaMasterDetails" type="{}VisaMasterDetailsComplexType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentMethodDetailsComplexType", propOrder = {
    "iDealDetails",
    "payPalDetails",
    "visaMasterDetails"
})
public class PaymentMethodDetailsComplexType {

    @XmlElement(name = "IDealDetails")
    protected IDealDetailsComplexType iDealDetails;
    @XmlElement(name = "PayPalDetails")
    protected PayPalDetailsComplexType payPalDetails;
    @XmlElement(name = "VisaMasterDetails")
    protected VisaMasterDetailsComplexType visaMasterDetails;

    /**
     * Gets the value of the iDealDetails property.
     * 
     * @return
     *     possible object is
     *     {@link IDealDetailsComplexType }
     *     
     */
    public IDealDetailsComplexType getIDealDetails() {
        return iDealDetails;
    }

    /**
     * Sets the value of the iDealDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link IDealDetailsComplexType }
     *     
     */
    public void setIDealDetails(IDealDetailsComplexType value) {
        this.iDealDetails = value;
    }

    /**
     * Gets the value of the payPalDetails property.
     * 
     * @return
     *     possible object is
     *     {@link PayPalDetailsComplexType }
     *     
     */
    public PayPalDetailsComplexType getPayPalDetails() {
        return payPalDetails;
    }

    /**
     * Sets the value of the payPalDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link PayPalDetailsComplexType }
     *     
     */
    public void setPayPalDetails(PayPalDetailsComplexType value) {
        this.payPalDetails = value;
    }

    /**
     * Gets the value of the visaMasterDetails property.
     * 
     * @return
     *     possible object is
     *     {@link VisaMasterDetailsComplexType }
     *     
     */
    public VisaMasterDetailsComplexType getVisaMasterDetails() {
        return visaMasterDetails;
    }

    /**
     * Sets the value of the visaMasterDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link VisaMasterDetailsComplexType }
     *     
     */
    public void setVisaMasterDetails(VisaMasterDetailsComplexType value) {
        this.visaMasterDetails = value;
    }

}
