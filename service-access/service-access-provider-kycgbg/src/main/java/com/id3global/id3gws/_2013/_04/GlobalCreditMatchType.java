
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalCreditMatch complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalCreditMatch"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GlobalCreditMatches" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalCreditMatches" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalCreditMatch", propOrder = {
    "globalCreditMatches"
})
public class GlobalCreditMatchType {

    @XmlElementRef(name = "GlobalCreditMatches", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalCreditMatchesType> globalCreditMatches;

    /**
     * Gets the value of the globalCreditMatches property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCreditMatchesType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalCreditMatchesType> getGlobalCreditMatches() {
        return globalCreditMatches;
    }

    /**
     * Sets the value of the globalCreditMatches property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCreditMatchesType }{@code >}
     *     
     */
    public void setGlobalCreditMatches(JAXBElement<ArrayOfGlobalCreditMatchesType> value) {
        this.globalCreditMatches = value;
    }

}
