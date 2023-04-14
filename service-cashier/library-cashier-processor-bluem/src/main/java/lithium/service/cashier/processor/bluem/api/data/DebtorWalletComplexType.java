
package lithium.service.cashier.processor.bluem.api.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DebtorWalletComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DebtorWalletComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="IDEAL" type="{}IDealComplexType"/>
 *         &lt;element name="PayPal" type="{}PayPalComplexType"/>
 *         &lt;element name="CreditCard" type="{}DebtorCreditCardComplexType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DebtorWalletComplexType", propOrder = {
    "ideal",
    "payPal",
    "creditCard"
})
public class DebtorWalletComplexType {

    @XmlElement(name = "IDEAL")
    protected IDealComplexType ideal;
    @XmlElement(name = "PayPal")
    protected PayPalComplexType payPal;
    @XmlElement(name = "CreditCard")
    protected DebtorCreditCardComplexType creditCard;

    /**
     * Gets the value of the ideal property.
     * 
     * @return
     *     possible object is
     *     {@link IDealComplexType }
     *     
     */
    public IDealComplexType getIDEAL() {
        return ideal;
    }

    /**
     * Sets the value of the ideal property.
     * 
     * @param value
     *     allowed object is
     *     {@link IDealComplexType }
     *     
     */
    public void setIDEAL(IDealComplexType value) {
        this.ideal = value;
    }

    /**
     * Gets the value of the payPal property.
     * 
     * @return
     *     possible object is
     *     {@link PayPalComplexType }
     *     
     */
    public PayPalComplexType getPayPal() {
        return payPal;
    }

    /**
     * Sets the value of the payPal property.
     * 
     * @param value
     *     allowed object is
     *     {@link PayPalComplexType }
     *     
     */
    public void setPayPal(PayPalComplexType value) {
        this.payPal = value;
    }

    /**
     * Gets the value of the creditCard property.
     * 
     * @return
     *     possible object is
     *     {@link DebtorCreditCardComplexType }
     *     
     */
    public DebtorCreditCardComplexType getCreditCard() {
        return creditCard;
    }

    /**
     * Sets the value of the creditCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link DebtorCreditCardComplexType }
     *     
     */
    public void setCreditCard(DebtorCreditCardComplexType value) {
        this.creditCard = value;
    }

}
