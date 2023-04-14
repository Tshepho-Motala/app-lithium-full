
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.globalcheck.ArrayOfGlobalStatus;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GetSupplierExtendedStatusInformationResult" type="{http://schemas.datacontract.org/2004/07/GlobalCheck.DataLib}ArrayOfGlobalStatus" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getSupplierExtendedStatusInformationResult"
})
@XmlRootElement(name = "GetSupplierExtendedStatusInformationResponse")
public class GetSupplierExtendedStatusInformationResponseElement {

    @XmlElementRef(name = "GetSupplierExtendedStatusInformationResult", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalStatus> getSupplierExtendedStatusInformationResult;

    /**
     * Gets the value of the getSupplierExtendedStatusInformationResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalStatus }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalStatus> getGetSupplierExtendedStatusInformationResult() {
        return getSupplierExtendedStatusInformationResult;
    }

    /**
     * Sets the value of the getSupplierExtendedStatusInformationResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalStatus }{@code >}
     *     
     */
    public void setGetSupplierExtendedStatusInformationResult(JAXBElement<ArrayOfGlobalStatus> value) {
        this.getSupplierExtendedStatusInformationResult = value;
    }

}
