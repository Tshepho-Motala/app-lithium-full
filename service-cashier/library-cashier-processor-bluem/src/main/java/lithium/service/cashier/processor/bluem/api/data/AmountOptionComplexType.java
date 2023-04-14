
package lithium.service.cashier.processor.bluem.api.data;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for AmountOptionComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AmountOptionComplexType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;>AmountSimpleType">
 *       &lt;attribute name="isPrefered" type="{}FlagSimpleType" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmountOptionComplexType", propOrder = {
    "value"
})
public class AmountOptionComplexType {

    @XmlValue
    protected BigDecimal value;
    @XmlAttribute(name = "isPrefered")
    protected Integer isPrefered;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Gets the value of the isPrefered property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsPrefered() {
        return isPrefered;
    }

    /**
     * Sets the value of the isPrefered property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsPrefered(Integer value) {
        this.isPrefered = value;
    }

}
