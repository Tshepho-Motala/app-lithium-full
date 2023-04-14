
package lithium.service.cashier.processor.bluem.api.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for DebtorReturnURLComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DebtorReturnURLComplexType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;>URLSimpleType">
 *       &lt;attribute name="automaticRedirect" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DebtorReturnURLComplexType", propOrder = {
    "value"
})
public class DebtorReturnURLComplexType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "automaticRedirect")
    protected Boolean automaticRedirect;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the automaticRedirect property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutomaticRedirect() {
        return automaticRedirect;
    }

    /**
     * Sets the value of the automaticRedirect property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutomaticRedirect(Boolean value) {
        this.automaticRedirect = value;
    }

}
