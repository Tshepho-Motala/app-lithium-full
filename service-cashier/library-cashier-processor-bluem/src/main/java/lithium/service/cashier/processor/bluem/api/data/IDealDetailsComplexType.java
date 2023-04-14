
package lithium.service.cashier.processor.bluem.api.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for IDealDetailsComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IDealDetailsComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DebtorAccountName" type="{}TokenLength70SimpleType" minOccurs="0"/>
 *         &lt;element name="DebtorIBAN" type="{}IBANAccountNumberSimpleType" minOccurs="0"/>
 *         &lt;element name="DebtorBankID" type="{}BICSimpleType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IDealDetailsComplexType", propOrder = {
    "debtorAccountName",
    "debtorIBAN",
    "debtorBankID"
})
public class IDealDetailsComplexType {

    @XmlElement(name = "DebtorAccountName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String debtorAccountName;
    @XmlElement(name = "DebtorIBAN")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String debtorIBAN;
    @XmlElement(name = "DebtorBankID")
    protected String debtorBankID;

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
     * Gets the value of the debtorIBAN property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDebtorIBAN() {
        return debtorIBAN;
    }

    /**
     * Sets the value of the debtorIBAN property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDebtorIBAN(String value) {
        this.debtorIBAN = value;
    }

    /**
     * Gets the value of the debtorBankID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDebtorBankID() {
        return debtorBankID;
    }

    /**
     * Sets the value of the debtorBankID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDebtorBankID(String value) {
        this.debtorBankID = value;
    }

}
