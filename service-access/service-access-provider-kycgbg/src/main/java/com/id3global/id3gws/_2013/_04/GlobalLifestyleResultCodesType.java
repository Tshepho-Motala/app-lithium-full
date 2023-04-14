
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalLifestyleResultCodes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalLifestyleResultCodes"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.id3global.com/ID3gWS/2013/04}GlobalItemCheckResultCodes"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="UKLifestyleCharacteristics" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PublicRiskScore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalLifestyleResultCodes", propOrder = {
    "ukLifestyleCharacteristics",
    "publicRiskScore"
})
public class GlobalLifestyleResultCodesType
    extends GlobalItemCheckResultCodesType
{

    @XmlElementRef(name = "UKLifestyleCharacteristics", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> ukLifestyleCharacteristics;
    @XmlElementRef(name = "PublicRiskScore", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> publicRiskScore;

    /**
     * Gets the value of the ukLifestyleCharacteristics property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUKLifestyleCharacteristics() {
        return ukLifestyleCharacteristics;
    }

    /**
     * Sets the value of the ukLifestyleCharacteristics property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUKLifestyleCharacteristics(JAXBElement<String> value) {
        this.ukLifestyleCharacteristics = value;
    }

    /**
     * Gets the value of the publicRiskScore property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPublicRiskScore() {
        return publicRiskScore;
    }

    /**
     * Sets the value of the publicRiskScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPublicRiskScore(JAXBElement<String> value) {
        this.publicRiskScore = value;
    }

}
