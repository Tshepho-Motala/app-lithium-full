
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalUKAddressDocuments complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalUKAddressDocuments"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ElectricitySupplier" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalElectricitySupplier" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalUKAddressDocuments", propOrder = {
    "electricitySupplier"
})
public class GlobalUKAddressDocumentsType {

    @XmlElementRef(name = "ElectricitySupplier", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalElectricitySupplierType> electricitySupplier;

    /**
     * Gets the value of the electricitySupplier property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalElectricitySupplierType }{@code >}
     *     
     */
    public JAXBElement<GlobalElectricitySupplierType> getElectricitySupplier() {
        return electricitySupplier;
    }

    /**
     * Sets the value of the electricitySupplier property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalElectricitySupplierType }{@code >}
     *     
     */
    public void setElectricitySupplier(JAXBElement<GlobalElectricitySupplierType> value) {
        this.electricitySupplier = value;
    }

}
