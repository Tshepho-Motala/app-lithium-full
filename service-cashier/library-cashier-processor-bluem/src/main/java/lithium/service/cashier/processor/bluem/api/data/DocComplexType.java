
package lithium.service.cashier.processor.bluem.api.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DocURL" type="{}URLSimpleType" minOccurs="0"/>
 *         &lt;element name="DocData" type="{}DocDataComplexType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocComplexType", propOrder = {
    "docURL",
    "docData"
})
public class DocComplexType {

    @XmlElement(name = "DocURL")
    @XmlSchemaType(name = "anyURI")
    protected String docURL;
    @XmlElement(name = "DocData")
    protected DocDataComplexType docData;

    /**
     * Gets the value of the docURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocURL() {
        return docURL;
    }

    /**
     * Sets the value of the docURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocURL(String value) {
        this.docURL = value;
    }

    /**
     * Gets the value of the docData property.
     * 
     * @return
     *     possible object is
     *     {@link DocDataComplexType }
     *     
     */
    public DocDataComplexType getDocData() {
        return docData;
    }

    /**
     * Sets the value of the docData property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocDataComplexType }
     *     
     */
    public void setDocData(DocDataComplexType value) {
        this.docData = value;
    }

}
