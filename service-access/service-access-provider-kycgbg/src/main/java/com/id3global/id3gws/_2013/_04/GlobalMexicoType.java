
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalMexico complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalMexico"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TaxIdentificationNumber" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMexicoTaxIdentificationNumber" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalMexico", propOrder = {
    "taxIdentificationNumber"
})
public class GlobalMexicoType {

    @XmlElementRef(name = "TaxIdentificationNumber", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalMexicoTaxIdentificationNumberType> taxIdentificationNumber;

    /**
     * Gets the value of the taxIdentificationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalMexicoTaxIdentificationNumberType }{@code >}
     *     
     */
    public JAXBElement<GlobalMexicoTaxIdentificationNumberType> getTaxIdentificationNumber() {
        return taxIdentificationNumber;
    }

    /**
     * Sets the value of the taxIdentificationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalMexicoTaxIdentificationNumberType }{@code >}
     *     
     */
    public void setTaxIdentificationNumber(JAXBElement<GlobalMexicoTaxIdentificationNumberType> value) {
        this.taxIdentificationNumber = value;
    }

}
