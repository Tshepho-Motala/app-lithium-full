
package lithium.service.cashier.processor.bluem.api.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for VisaMasterDetailsComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VisaMasterDetailsComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DebtorAccountName" type="{}TokenLength70SimpleType" minOccurs="0"/>
 *         &lt;element name="DebtorCard" type="{}TokenLength70SimpleType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VisaMasterDetailsComplexType", propOrder = {
    "debtorAccountName",
    "debtorCard"
})
public class VisaMasterDetailsComplexType {

    @XmlElement(name = "DebtorAccountName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String debtorAccountName;
    @XmlElement(name = "DebtorCard")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String debtorCard;

    /**
     * Gets the value of the debtorAccountName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDebtorAccountName() {
        return debtorAccountName;
    }

    /**
     * Sets the value of the debtorAccountName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDebtorAccountName(String value) {
        this.debtorAccountName = value;
    }

    /**
     * Gets the value of the debtorCard property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDebtorCard() {
        return debtorCard;
    }

    /**
     * Sets the value of the debtorCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDebtorCard(String value) {
        this.debtorCard = value;
    }

}
