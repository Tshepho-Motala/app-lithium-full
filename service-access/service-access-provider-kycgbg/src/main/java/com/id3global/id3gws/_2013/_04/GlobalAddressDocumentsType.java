
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalAddressDocuments complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalAddressDocuments"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="UKAddressDocuments" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalUKAddressDocuments" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalAddressDocuments", propOrder = {
    "ukAddressDocuments"
})
public class GlobalAddressDocumentsType {

    @XmlElementRef(name = "UKAddressDocuments", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalUKAddressDocumentsType> ukAddressDocuments;

    /**
     * Gets the value of the ukAddressDocuments property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalUKAddressDocumentsType }{@code >}
     *     
     */
    public JAXBElement<GlobalUKAddressDocumentsType> getUKAddressDocuments() {
        return ukAddressDocuments;
    }

    /**
     * Sets the value of the ukAddressDocuments property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalUKAddressDocumentsType }{@code >}
     *     
     */
    public void setUKAddressDocuments(JAXBElement<GlobalUKAddressDocumentsType> value) {
        this.ukAddressDocuments = value;
    }

}
